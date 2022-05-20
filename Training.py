import tensorflow as tf
from tensorflow import keras
import matplotlib.pyplot as plt
from tensorflow.keras.applications import ResNet101V2, ResNet50, ResNet50V2, DenseNet121, DenseNet201, ResNet152V2, VGG16, NASNetMobile, EfficientNetB3, VGG19, DenseNet169
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from keras.layers import Input
from sklearn.metrics import classification_report
import numpy as np

train_dir = '/trainingsetdirectory'

validation_dir = '/validationdirectory'

test_dir = '/testdirectory'

batch_size = 32
num_classes = 331

hyperparameters = {
    'learning_rate':[0.1, 0.01],
    'decay':[1e-2, 1e-4, 1e-6, 1e-8],
    'color':['grayscale','rgb'],
    'size':[75,150,224]
}

for color in hyperparameters['color']:
    for size in hyperparameters['size']:
        if color in 'grayscale':
            channel = 1
        else:
            channel = 3
        input_shape = Input(shape=(size, size, channel))
        cnn_models = [
            VGG16(include_top=True, weights=None, input_shape=input_shape, pooling='max', classes=num_classes),
            ResNet50V2(include_top=True, weights=None, input_shape=input_shape, pooling='avg', classes=num_classes),
            ResNet101V2(include_top=True, weights=None, input_shape=input_shape, pooling='avg', classes=num_classes),
            ResNet152V2(include_top=True, weights=None, input_shape=input_shape, pooling='avg', classes=num_classes),
            DenseNet121(include_top=True, weights=None, input_shape=input_shape, pooling='avg', classes=num_classes),
            DenseNet169(include_top=True, weights=None, input_shape=input_shape, pooling='avg', classes=num_classes),
            DenseNet201(include_top=True, weights=None, input_shape=input_shape, pooling='avg', classes=num_classes),
            EfficientNetB3(include_top=True, weights=None, input_shape=input_shape, pooling='avg', classes=num_classes)
        ]
        for model in cnn_models:
            for lr in hyperparameters['learning_rate']:
                for decay in hyperparameters['decay']:
                    train_data = ImageDataGenerator(rescale=1.0 / 255)
                    train_data = train_data.flow_from_directory(directory=train_dir,
                                                                target_size=(size, size),
                                                                batch_size=batch_size,
                                                                class_mode='categorical',
                                                                color_mode=color
                                                                )

                    validation_data = ImageDataGenerator(rescale=1.0/255)
                    validation_data = validation_data.flow_from_directory(
                        directory=validation_dir,
                        target_size=(size, size),
                        batch_size=batch_size,
                        class_mode='categorical',
                        color_mode=color
                    )

                    path = '/savemodeldir/' + '_color_' + str(color) + '_size_' + str(size) + '_model_' + str(model.name) + '_lr_' + str(lr) + '_decay_' + str(decay)

                    es = tf.keras.callbacks.EarlyStopping(monitor='val_loss', mode='min', verbose=1, patience=60)
                    save = tf.keras.callbacks.ModelCheckpoint(path+'.h5', monitor='val_loss', mode='min', save_best_only=True)
                    callback = [save, es]
                    opt = keras.optimizers.SGD(learning_rate=lr, momentum=0.9, decay=decay)

                    model.compile(optimizer=opt, loss='categorical_crossentropy', metrics=['accuracy'])

                    history = model.fit(train_data,
                                        validation_data=validation_data,
                                        epochs=200
                                        ,
                                        callbacks=callback
                                        )

                    model.save(path+'.h5')

                    plt.figure(figsize=(20, 5))
                    plt.subplot(1, 2, 1)
                    plt.plot(history.history['accuracy'], 'black')
                    plt.plot(history.history['val_accuracy'], 'gray')
                    plt.legend(['train_accuracy', 'validation_accuracy'], loc='lower right')

                    plt.subplot(1, 2, 2)
                    plt.plot(history.history['loss'], 'black')
                    plt.plot(history.history['val_loss'], 'gray')
                    plt.legend(['train_loss', 'validation_loss'], loc='upper right')
                    # plt.show()
                    plt.savefig(path+'.png')
    test_data = ImageDataGenerator(rescale=1.0 / 255)
    test_data = test_data.flow_from_directory(directory=test_dir,
                                              target_size=size,
                                              batch_size=batch_size,
                                              shuffle=False,
                                              class_mode='categorical',
                                              color_mode=color
                                              )
    scores = model.evaluate(test_data)
    Y_pred = model.predict_generator(test_data)
    classes = test_data.classes[test_data.index_array]
    y_pred = np.argmax(Y_pred, axis=-1)
    print(classification_report(y_pred, classes))
    a = model.history
    print(a)