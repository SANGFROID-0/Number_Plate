import cv2
import numpy as np
import base64
import requests
import json
from ultralytics import YOLO

# 1. Initialize Detection Model (YOLOv8)
detector = YOLO('yolov8n.pt') 

# Define your local Ollama endpoint and model
OLLAMA_API_URL = "http://localhost:11434/api/chat"
OLLAMA_MODEL = "glm-ocr:latest" # Ensure this matches the exact name you pulled in Ollama

def align_plate(image_crop):
    """
    Detects the angle of the plate and rotates it to be horizontal.
    """
    gray = cv2.cvtColor(image_crop, cv2.COLOR_BGR2GRAY)
    blur = cv2.GaussianBlur(gray, (5, 5), 0)
    edges = cv2.Canny(blur, 50, 150)
    
    contours, _ = cv2.findContours(edges, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    
    if not contours:
        return image_crop 
        
    largest_contour = max(contours, key=cv2.contourArea)
    rect = cv2.minAreaRect(largest_contour)
    angle = rect[-1]
    
    if angle < -45:
        angle = -(90 + angle)
    else:
        angle = -angle
        
    (h, w) = image_crop.shape[:2]
    center = (w // 2, h // 2)
    M = cv2.getRotationMatrix2D(center, angle, 1.0)
    aligned_plate = cv2.warpAffine(image_crop, M, (w, h), flags=cv2.INTER_CUBIC, borderMode=cv2.BORDER_REPLICATE)
    
    return aligned_plate

def read_plate_with_ollama(image_array):
    """
    Encodes the aligned crop to Base64 and sends it to the local Ollama GLM-OCR model.
    """
    # Convert OpenCV image to JPG, then to Base64
    _, buffer = cv2.imencode('.jpg', image_array)
    img_b64 = base64.b64encode(buffer).decode('utf-8')
    
    # Strict prompt to force clean output
    prompt_text = "Extract the license plate number from this image. Return ONLY the alphanumeric text. No spaces, no punctuation, no extra words."
    
    payload = {
        "model": OLLAMA_MODEL,
        "messages": [
            {
                "role": "user",
                "content": prompt_text,
                "images": [img_b64]
            }
        ],
        "stream": False,
        "options": {
            "num_ctx": 16384,   # CRITICAL for GLM-OCR to process images without crashing
            "temperature": 0.1  # Low temperature for deterministic, factual extraction
        }
    }
    
    try:
        response = requests.post(OLLAMA_API_URL, json=payload)
        response.raise_for_status()
        data = response.json()
        return data.get('message', {}).get('content', '').strip()
    except requests.exceptions.RequestException as e:
        print(f"Error communicating with Ollama: {e}")
        return ""

def process_image(image_path):
    img = cv2.imread(image_path)
    
    # Step 1: Detect Plate
    results = detector(img, conf=0.5)
    output_data = []
    
    for result in results:
        boxes = result.boxes
        for box in boxes:
            x1, y1, x2, y2 = map(int, box.xyxy[0])
            confidence = float(box.conf[0])
            
            # Step 2: Extract and Align
            plate_crop = img[y1:y2, x1:x2]
            aligned_plate = align_plate(plate_crop)
            
            # Step 3: OCR via Local Ollama
            plate_text = read_plate_with_ollama(aligned_plate)
            
            # Step 4: Prepare Data for JSON
            output_data.append({
                "plate_number": plate_text.upper().replace(" ", ""),
                "detection_confidence": round(confidence, 3),
                "bounding_box": {"x1": x1, "y1": y1, "x2": x2, "y2": y2}
            })

    # Return as formatted JSON string
    return json.dumps({"results": output_data}, indent=4)

# --- Execution ---
if __name__ == "__main__":
    # Ensure Ollama is running in the background before executing
    json_output = process_image("np.jpg")
    print(json_output)
    pass