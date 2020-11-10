# project
## OpenCV, ocr을 활용한 영어 단어 학습 어플리케이션
------------------------------------

#### 스캐너 기능 

<img src = "https://user-images.githubusercontent.com/18055781/98684543-a505d480-23a9-11eb-9ace-8f404aedb445.png"></img>

- 컴퓨터 비젼 기술인 OpenCV를 이용해 스캐너 기능 구현해 사진을 찍으면 인식된 이미지를 새로운 스캔 이미지로 저장되게 했습니다.


#### 문장 인식 해석 기능

<img src = "https://user-images.githubusercontent.com/18055781/98684739-e1393500-23a9-11eb-8b93-6e2d5eb759e0.png"></img>

- 컴퓨터 비젼 기술인 OCR을 이용해 카메라로 찍은 글자를 인식하고 인식한 글자를 번역해주는 기능을 구현했습니다. 

번역은 파파고 API를 사용하여 해석 기능을 구현했습니다. 

#### 단어 토큰화하여 단어장에 자동 저장 기능

<img src = "https://user-images.githubusercontent.com/18055781/98684780-ebf3ca00-23a9-11eb-9214-543c3884174e.png"></img>

- 안드로이드의 room 데이터베이스를 구축하여 OCR로 인식된 문장의 단어들을 뜻과 함께 데이터베이스에 자동 저장 됩니다.

#### 단어 학습 기능

<img src = "https://user-images.githubusercontent.com/18055781/98684898-12196a00-23aa-11eb-81f4-c70736056f2a.png"></img>

<img src = "https://user-images.githubusercontent.com/18055781/98684845-00d05d80-23aa-11eb-9e01-6e19d10ee639.png"></img>

- 저장된 단어를 카드 형식으로 볼 수 있고 사용자가 단어를 편집할 수 있습니다. 단어를 암기하고 사지선다 형식의 시험을 볼 수 있습니다. 
