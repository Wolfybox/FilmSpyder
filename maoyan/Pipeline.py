import json as Json
import os
import pymysql


class MoviePipeline:
    """
    期望获取一个电影的票房数据，输出到文件中
    """
    def __init__(self, movie):
        # self._dir = "/data/" + str(movie[0])
        # ex = os.path.exists("/home/xuan-ices/Desktop"+self._dir)
        # if not ex:
        #     os.makedirs("/home/xuan-ices/Desktop"+self._dir)
        self._data = movie

    # def tofile(self):
    #     jsondir="/home/xuan-ices/Desktop"+self._dir+"/basic_info.txt"
    #
    #     with open(jsondir, "w") as base:
    #         title = ["id", "name", "director", "box", "class", "year", "month", "day", "intro"]
    #         basic = dict(zip(title, self._data[:-1]))
    #         json = Json.dumps(basic,  ensure_ascii=False)
    #         print(json)
    #         base.write(str(json))
    #
    #     logodir = "/home/xuan-ices/Desktop"+self._dir+"/logo.txt"
    #     with open(logodir, "w") as logo:
    #         logo.write(str(self._data[-1]))

    def toserver(self):
        moviedb = pymysql.connect(host='193.112.48.152', user='root', passwd='LJH787807080886', db='MoviesDatabase', port=3306, charset='utf8')
        cursor = moviedb.cursor()
        a = self._data
        actors = ','.join(a[10][1:11])
        #输出到数据库：[id, name, director, box, movie_class, year, month, day, intro, logourl, actors, rate]
        sql = "INSERT IGNORE INTO Movie(moviename,director,box_office,class,year,month,day,introduce,thumburl,actor,rate) VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',%s)" % (a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], actors, a[11])
        print(sql)

        try:
            cursor.execute(sql)
            moviedb.commit()
        except:
            moviedb.rollback()

        moviedb.close()





