import cv2
import flask
from flask import send_file
import werkzeug
import time
from tensorflow.keras import models
import numpy as np
from PIL import Image
import os

app = flask.Flask(__name__)


@app.route('/', methods=['GET', 'POST'])
def handle_request():

    files_ids = list(flask.request.files)
    print("\nNumber of Received Images : ", len(files_ids))
    image_num = 1
    for file_id in files_ids:
        print("\nSaving Image ", str(image_num), "/", len(files_ids))
        imagefile = flask.request.files[file_id]
        filename = werkzeug.utils.secure_filename(imagefile.filename)
        print("Image Filename : " + imagefile.filename)
        timestr = time.strftime("%Y%m%d-%H%M%S")
        imagefile.save("/save/"+timestr+'_'+filename)
        image_num = image_num + 1
    print("\n")
    t = imagefile.filename.split("_")
    test_dir = '/testdiretory'

    model = models.load_model('densenet201.h5')

    recent_file_name = "/save/" + timestr + '_' + filename

    def load(file_name):
        np_image = file_name.convert("L")
        np_image = np_image.resize((224, 224), Image.ANTIALIAS)
        np_image = np.array(np_image).astype('float32') / 255.0
        np_image = np.expand_dims(np_image, axis=0)

        return np_image

    def load_save(file_name):
        np_image2 = file_name.convert("L")
        np_image2 = np_image2.resize((224, 224), Image.ANTIALIAS)
        np_image2 = np.array(np_image2)
        im = Image.fromarray(np_image2)
        im.save("/save_processing/" + timestr + '_' + filename, format='JPEG', quality=100)

    def xyChange(file_name):
        np_image3 = Image.open(file_name)
        image3_size = list(np_image3.size)
        if image3_size[0] > image3_size[1]:
            re_image = np_image3.rotate(-90)
        elif image3_size[1] < image3_size[0]:
            re_image = np_image3.rotate(90)
        else:
            re_image = np_image3
        return re_image

    def image_cleaning(file_name):
        img = cv2.imread(file_name)
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        otsu = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)[1]

        cv2.imwrite("/cleaning/" + timestr + '_' + filename, otsu)

    image_cleaning(recent_file_name)
    cleaning_dir = "/cleaning/" + timestr + '_' + filename
    image2 = xyChange(cleaning_dir)
    image1 = load(image2)
    result = model.predict(image1)
    num_list = list(result[0])
    num_list2 = list(result[0])
    num_list.sort(reverse=True)
    num_list5 = num_list[0:5]  # 최상위 5개
    num_list5_sort = []

    for i in range(5):
        num_list5_sort.append(num_list2.index(num_list5[i]))
    num_list5_sort.sort()
    max1 = 0
    for i in range(len(num_list2)):
        if max1 < num_list2[i]:
            max1 = num_list2[i]
            num = i

    class_names = os.listdir(test_dir)
    class_names.sort()
    name_list = []
    for i in range(len(class_names)):
        for k in range(len(num_list5)):
            find = num_list2.index(num_list5[k])
            if find == i:
                name_list.append(class_names[i])
    name_list.sort()
    cnt = 0
    for n in num_list5:
        if n*100.0 > 1.00:
            cnt += 1
    predict_result = ""
    for c in range(0, cnt, 1):
        predict_result += name_list[c] + ":{:.2f}%".format(num_list5[c]*100.0)
    print(predict_result)

    load_save(image2)
    return predict_result


@app.route('/img/<id>', methods=['GET', 'POST'])
def image_send(id):
    print(id)
    dir = '/datasetdir/'
    list3 = []
    list1 = os.listdir(dir)

    for i in range(len(list1)):

        if list1[i] == '.DS_Store':
            continue
        list3.append(str(list1[i]))

    list3.sort()

    for i in range(len(list3)):
        if list3[i] == id:
            list11 = os.listdir(dir + list3[i] + '/')
            print(list11)
            image_name = list11[0]
    full_dir = dir + id + '/' + image_name

    return send_file(full_dir, mimetype='image/jpg')


app.run(host="hostip", port=5000, debug=True)