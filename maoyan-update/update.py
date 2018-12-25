from crawler import CrawlerMaoYan, CrawlerActor
import threading
import pymysql
def clear():
    moviedb = pymysql.connect(host='193.112.48.152', user='root', passwd='LJH787807080886', db='MoviesDatabase',
                              port=3306, charset='utf8')
    cursor = moviedb.cursor()

    sql = 'truncate table TempMovie'#快速、不可恢复地删除数据
    try:
        cursor.execute(sql)
        moviedb.commit()
    except:
        moviedb.rollback()
    moviedb.close()


def update():
    clear()
    url = "https://piaofang.maoyan.com/?ver=normal"
    maoyan = CrawlerMaoYan(url)
    actors = maoyan.crawl()
    crawler_actor = CrawlerActor(actors)
    crawler_actor.crawl()

if __name__ == "__main__":
    t = threading.Timer(86400, update)
    t.start()
