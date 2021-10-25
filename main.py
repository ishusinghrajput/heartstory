import face_recognition
import cv2
from face_recognition.api import face_distance
import numpy as np
import os

path = 'Images'
images = []
classNames = []
myList = os.listdir(path)

for cl in myList:
    curImg = cv2.imread(f'{path}/{cl}')
    images.append(curImg)
    classNames.append(os.path.splitext(cl)[0])

def findEncodings(images):
    encodeList = []
    for img in images:
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        encode = face_recognition.face_encodings(img)
        encodeList.append(encode)
    return encodeList

encodeListKnown = findEncodings(images)
print('Encoding Completed!')


cap = cv2.VideoCapture(0)

while True:
    success, img = cap.read()
    imgS = cv2.resize(img,(0,0),None,0.25,0.25)
    imgS = cv2.cvtColor(imgS, cv2.COLOR_BGR2RGB)

    faceCurrentFrame = face_recognition.face_locations(imgS)
    encodeCurrentFrame = face_recognition.face_encodings(imgS , faceCurrentFrame)


    for encodeFace, faceLoc in zip(encodeCurrentFrame , faceCurrentFrame):
        
        matches = face_recognition.compare_faces(encodeListKnown , encodeFace)
        faceDis = face_recognition.face_distance(encodeListKnown , encodeFace)
        matchIndex = np.argmin(faceDis)

        if matches[matchIndex]:
            name = classNames[matchIndex].upper()
            print(name)
            y1,x2,y2,x1 = faceDis
            y1,x2,y2,x1 = y1*4,x2*4,y2*4,x1*4
            cv2.rectangle(img , (x1,y1), (x2,y2) , (0,255,0), 2)
            cv2.rectangle(img , (x1,y2-35), (x2,y2) , (0,255,0), cv2.FILLED)
            cv2.putText(img, name , (x1+6, y2-6) , cv2.FONT_HERSHEY_COMPLEX , 1 , (255,255,255) , 2)
            


    cv2.imshow('WebCam' , img)
    cv2.waitKey(1)

