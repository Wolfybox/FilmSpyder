import pymysql


class MoviePipeline:
    """
    期望获取一个电影的票房数据，输出到文件中
    """
    def __init__(self, movie):
        self._data = movie

    def toserver(self):
        moviedb = pymysql.connect(host='193.112.48.152', user='root', passwd='LJH787807080886', db='MoviesDatabase', port=3306, charset='utf8')
        cursor = moviedb.cursor()
        a = self._data
        actors = ','.join(a[10][1:11])
        #输出到数据库：[id, name, director, box, movie_class, year, month, day, intro, logourl, actors, rate]
        sql = "INSERT INTO TempMovie(moviename,director,box_office,class,year,month,day,introduce,thumburl,actor,rate) SELECT '%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',%s FROM DUAL WHERE NOT EXISTS(SELECT moviename FROM TempMovie WHERE moviename='%s')" % (a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], actors, a[11], a[1])
        print(sql)

        try:
            cursor.execute(sql)
            moviedb.commit()
        except:
            moviedb.rollback()
        moviedb.close()


