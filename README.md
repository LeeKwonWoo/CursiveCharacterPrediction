# CursiveCharacterPrediction


Cursive character (CC) recognition faces challenges owing to multifarious CC writing styles and complex character structures. Therefore, a universal CC identification system is essential. 
This paper proposes a novel deep learning-based CC prediction model and describes the development and implementation of a corresponding mobile application.
The proposed model comprises three primary steps: data collection (in which images are collected and cleaned); image preprocessing (where images are normalized, resized, and augmented); and prediction (involving hyperparameter optimization, regularization, and prediction by deep learning models). Transfer learning from convolutional neural networks such as ResNet, DenseNet, VGG, and EfficientNet is also employed. The model was comparatively evaluated on a dataset comprising Wang Xizhi's Grass Jue Song and Cursive Thousand Characters images. Using DenseNet-201, it exhibited superior performance, with accuracy, precision, recall, and F1-score of 0.92, 0.92, 0.93, and 0.92, respectively. Furthermore, the CC prediction application allows users to input CCs and obtain accurate prediction results.

## Getting started
#### Setup Environment
  * Clone the repository: `git clone https://github.com/LeeKwonWoo/CursiveCharacterPrediction.git`
  * Installed the requierement: 
  	  - `Python version 3.8.0`
 	   - `TensorFlow 2.5.0 library`
     - `CUDA-Toolkit 11.0`
     - `Virtual device with Android version 9`
     - `Model’s name Nexus 5X, API 28`
     - `Application's SDK was 30`

### Preprare the dataset
 After downloading the datasets from Zenodo, place place each subset in the specified directories:
   - `/trainingsetdirectory/`
   - `/validationdirectory/`
   - `/testdirectory/`

### Usage
#### Training the Model
To train the model and verify the reported results, run:
```
python Training.py
```
#### Testing the Model
Once the model is trained, you can test its performance by running:
```
python PredictServer.py
```
#### Android Implementation
To explore the Android implementation, open the Cursive_Character/ directory in Android Studio. This directory contains all the code, resources, and build scripts needed to run the mobile application. 
Make sure your Android Virtual Device (AVD) matches the specified configuration to properly test the app.

#### Notes on Reproducibility
   - `Environment Consistency:` Ensure you use Python 3.8.0 and TensorFlow 2.5.0 to match the original training conditions.
   - `Dataset Version:` Always download the exact version of the dataset from the provided DOI link to guarantee consistency with the reported results.
   - `Virtual Device Configuration:` Use the specified virtual device settings (Nexus 5X, Android 9, API 28) and application SDK version 30 to replicate the environment where the model was deployed and tested.


#### Reference:
(Not Yet availabel) 
