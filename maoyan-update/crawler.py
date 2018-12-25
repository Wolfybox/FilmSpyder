import requests
from parsers import ParseList, ParseError, ParserActor
import pymysql


class CrawlerMaoYan:
    """猫眼页面抓取，抓取电影票房。"""

    def __init__(self, url):
        self._url = url
        self._headers = {
            'Host': 'piaofang.maoyan.com',
            'Connection': 'keep-alive',
            'Cache-Control': 'max-age=0',
            'Upgrade-Insecure-Requests': '1',
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) '
                          'Chrome/62.0.3202.75 Safari/537.36',
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8',
            'Referer': 'http://piaofang.maoyan.com/dashboard',
            'Accept-Encoding': 'gzip, deflate',
            'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7',
            'Cookie': '_lxsdk_s=b612e13fda305b2dadde4a6974ab%7C%7C2'
        }

    def crawl(self):
        """抓取票房页面。"""
        r = requests.get(self._url, headers=self._headers)
        try:
            r.raise_for_status()
        except requests.exceptions.HTTPError:
            raise requests.exceptions.HTTPError
        else:
            try:
                allactors = ParseList.parse(r.text)
                return allactors
            except ParseError:
                print('Parse error')
                return []



class CrawlerActor:
    """
    爬取所有演员详情
    """
    def __init__(self, actorlist):
        self._list = actorlist
        self._baseurl = 'https://piaofang.maoyan.com/celebrity?id='
        self._headers = {
            'Host': 'piaofang.maoyan.com',
            'Connection': 'keep-alive',
            'Upgrade-Insecure-Requests': '1',
            'User-Agent': 'Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:63.0) Gecko/20100101 Firefox/63.0',
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
            'Accept-Encoding': 'gzip, deflate, br',
            'Accept-Language': 'en-US,en;q=0.5',
            'TE': 'Trailers',
            'Cookie': '_lxsdk_cuid=167162b0454c8-03d2e9868bac6c-74266752-1aeaa0-167162b0455c8; _lxsdk=167162b0454c8-03d2e9868bac6c-74266752-1aeaa0-167162b0455c8; __mta=186899991.1542265177255.1544081680796.1544081858382.15; _lxsdk_s=1678270a63c-5e1-f9b-b2d%7C%7C20; theme=moviepro; _lx_utm=utm_source%3DBaidu%26utm_medium%3Dorganic'
        }

    def crawl(self):
        i = 0
        db = pymysql.connect(host='193.112.48.152', user='root', passwd='LJH787807080886', db='MoviesDatabase',
                             port=3306, charset='utf8')
        cursor = db.cursor()

        for actorid in self._list:
            print(i)
            i += 1
            try:
                print(self._baseurl+str(actorid))
                print(self._headers)
                r = requests.get(self._baseurl+str(actorid), headers=self._headers)
            except requests.exceptions.HTTPError:
                raise requests.exceptions.HTTPError
            else:
                actor = ParserActor.parse(r.text, actorid)
                print(actor)


                sql = "INSERT INTO Actor(actorname,year,month,day,imageurl,sex,maoyanid) SELECT '%s','%s','%s','%s','%s','%s','%s' FROM DUAL WHERE NOT EXISTS(SELECT maoyanid FROM Actor WHERE maoyanid='%s')" % (
                                                        actor[1], actor[2], actor[3], actor[4], actor[5], actor[6], actor[7], actor[7])
                print(sql)
                try:
                    cursor.execute(sql)
                    db.commit()
                except:
                    db.rollback()

        db.close()