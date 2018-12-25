import requests
from lxml import html
import re
import json

class MaoyanDetailCrawler:
    def __init__(self, url):
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
        self._url = url
        self._extendedUrl = url+"/moresections"
        self._actorurl = url+"/celebritylist"

    def crawl(self):
        """
        crawling the page
        :return: information
        """

        id = self._url.split("/")[4]
        r1 = requests.get(self._url, headers=self._headers)
        try:
            r1.raise_for_status()
        except requests.exceptions.HTTPError:
            print("cannot connect "+self._url)
        else:
            page_parser = MaoyaoDetailParser(r1.text)

            logourl = page_parser.get_logo()
            name = page_parser.get_name()
            if name == '宝贝，对不起':
                return '-1', '', '', '', '', '', '', '', '', ''
            print('name:', name, '|')

            rate = page_parser.get_rate()
            print('rate:', rate)
            movie_class = page_parser.get_class()
            print('class:', movie_class, '|')
            try:
                year, month, day = page_parser.get_date()
            except:
                year = '2010'
                month = ''
                day = ''
            print('date:', year, month, day, '|')

            r2 = requests.get(self._extendedUrl, headers=self._headers)
            try:
                r2.raise_for_status()
            except requests.exceptions.HTTPError:
                print("cannot connect json file", self._extendedUrl)
                return id, name, '', movie_class, year, month, day, '', logourl, rate
            else:
                eparser = ExtensionParser(json.loads(r2.text))
                intro = eparser.get_intro()
                print('intro:'+intro)
                director = eparser.get_director()
                print('director:'+director)

                return id, name, director, movie_class, year, month, day, intro, logourl, rate


    def getactors(self):
        """
        获取一个电影所有演员的标识以供后期处理
        :return:一个存有一个电影所有演员id的数组，第一个演员是导演
        """
        r2 = requests.get(self._actorurl, headers=self._headers)
        try:
            r2.raise_for_status()
        except requests.exceptions.HTTPError:
            pass
        else:
            page = r2.text
            tree = html.fromstring(page)

            try:
                actor_num = int(str(tree.xpath('/html/body/div[2]/div/div/div[1]/dl[2]/dt/div/span[1]/em/text()')[0]).replace("(","").replace(")",""))
                print(actor_num)
                actor_num+=1

                #注意第一个演员是导演
                actors = tree.xpath('//a[@class="p-link"]/@data-id')[0:actor_num]
                print(len(actors))
                return actors

            except:
                print('Error at', self._actorurl)
                return []




class MaoyaoDetailParser:
    """
    parse the detail page of one movie

    """
    def __init__(self,html_text):
        self._tree = html.fromstring(html_text)

    def get_logo(self):
        try:
            origin = str(self._tree.xpath('/html/body/div[2]/section[1]/div[1]/div[3]/div[1]/div[1]/img/@src')[0]).strip()
        except:
            return ''
        else:
            point = origin.index('webp')
            src = 'http:' + origin[0:point]
            return src[:-1]

    def get_name(self):
        return str(self._tree.xpath('//span[@class="info-title-content"]/text()')[0]).strip()

    def get_class(self):
        try:
            return str(self._tree.xpath('//p[@class="info-category"]/text()')[0]).strip()
        except:
            return str(self._tree.xpath('//span[@class="info-subtype ellipsis-1"]/text()'))

    def get_rate(self):
        try:
            return str(self._tree.xpath('//span[@class="rating-num"]/text()')[0]).strip()
        except:
            return ''


    def get_date(self):
        date = self._tree.xpath('//span[@class="score-info ellipsis-1"]/text()')[0].strip()
        date = date[0:10]
        year = date.split("-")[0]
        month = date.split("-")[1]
        day = date.split("-")[2]
        return year, month, day





class ExtensionParser:
    """
    parse extended information
    '//p[@class="title ellipsis-1"]/text()'
    """
    def __init__(self,Jsonin):
        self._dic = Jsonin

    def get_intro(self):
        text = self._dic['sectionHTMLs']['detailSection']['html']
        try:
            tree = html.fromstring(text)
            return str(tree.xpath('//div[@class="detail-block-content"]/text()')[0]).strip()
        except:
            return ''

    def get_director(self):
        text = self._dic['sectionHTMLs']['celebritySection']['html']
        try:
            tree = html.fromstring(text)
        except:
            return ''
        else:
            #return str(tree.xpath('//a/@href')[1]).split("/")[2]
            return str(tree.xpath('//p[@class="title ellipsis-1"]/text()')[0]).strip()




class MaoyanCelebritylist:
    def __init__(self, text):
        self._tree = html.fromstring(text)
    def get_actors(self):
        pass
    def get_directors(self):
        pass