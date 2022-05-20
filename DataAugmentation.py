from tensorflow.keras.preprocessing.image import img_to_array, load_img

import os
import shutil

from tensorflow.keras.preprocessing.image import ImageDataGenerator


class Data:

    # dataset

    train_dir = '/trainingsetdirectory'

    validation_dir = '/validationdirectory'

    test_dir = '/testdirectory'

    batch_size = 32
    img_height = 224
    img_width = 224
    color = ['grayscale', 'rgb']
    img_size = [(224, 224), (150, 150), (75, 75)]


    test_data = ImageDataGenerator(rescale=1.0 / 255)
    test_data = test_data.flow_from_directory(directory=test_dir,
                                              target_size=img_size[0],
                                              batch_size=batch_size,
                                              shuffle=False,
                                              class_mode='categorical',
                                              color_mode=color[0],
                                              )
    list3 = []

    list1 = os.listdir(validation_dir)  # 한자 목록들

    for i in range(len(list1)):

        if list1[i] == '.DS_Store':
            continue

        list2 = os.listdir(validation_dir + list1[i] + '/')  # 파일당 이미지 목록

        if len(list2) != 90:
            list3.append(str(list1[i]))
    list3.sort()

    for i in list3:
        list2 = os.listdir(test_dir + i + '/')
        list2.sort()
        for k in range(0, 1, 1):
            shutil.move(test_dir + i + '/' +list2[k], train_dir + i + '/')


    train_data2 = ImageDataGenerator(rescale=1. / 255.,
                                         rotation_range=10,
                                         width_shift_range=0.1,
                                         height_shift_range=0.1,
                                         zoom_range=0.1,
                                         shear_range=0.01,
                                         brightness_range=(0.2, 0.2))

    for i in range(len(list3)):
        nnn1 = 0
        nnn2 = 0
        # c = 150
        list4 = os.listdir(train_dir + list3[i])
        # print(list4)
        # print(len(list4))
        if 301 <= len(list4):
            continue
        n3 = 300 - len(list4)
        n1 = n3 // len(list4)
        n2 = n3 % len(list4)
        q1 = n1+1
        q2 = len(list4) - n2

        list4.sort()
        nnn2 = n2 * q1
        nnn3 = q2 * n1

        for im in range(len(list4)):
            s = list4[im].split('.')
            if im < n2:
                if s[1] == 'jpg':
                    img = load_img(train_dir + list3[i] + '/' + list4[im])
                    x = img_to_array(img)
                    x = x.reshape((1,) + x.shape)
                    j = 0
                    for batch in train_data2.flow(x, batch_size=3, save_to_dir=train_dir + list3[i] + '/', save_prefix=s[0],
                                                  save_format='jpg'):
                        j += 1
                        nnn1 += 1
                        if j >= q1:
                            break

            else:
                q1 = n1
                if q1 == 0:
                    break
                if s[1] == 'jpg':
                    img = load_img(train_dir + list3[i] + '/' + list4[im])
                    x = img_to_array(img)
                    x = x.reshape((1,) + x.shape)
                    j = 0
                    for batch in train_data2.flow(x, batch_size=3, save_to_dir=train_dir + list3[i] + '/', save_prefix=s[0],
                                                  save_format='jpg'):
                        j += 1
                        nnn1 += 1
                        if j >= q1:
                            break