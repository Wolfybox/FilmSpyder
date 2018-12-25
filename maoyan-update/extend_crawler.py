import requests
from lxml import html
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
        self._extendedUrl = url.split('?')[0] +"/moresections"
        self._actorurl = url.split('?')[0] +"/celebritylist"

    def crawl(self):
        """
        crawling the page
        :return: information
        其中，box未破解
        """
        id = self._url.split("/")[4]
        r1 = requests.get(self._url, headers=self._headers)
        try:
            r1.raise_for_status()
        except requests.exceptions.HTTPError:
            print("cannot connect "+self._url)
        else:
            page_parser = MaoyaoDetailParser(r1.text)

            box = page_parser.get_tempbox()#获取票房
            print('box:', box)

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
            print(self._extendedUrl)

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
                actors = eparser.get_majoractors()
                print('actors:', actors)


                return id, name, director, box, movie_class, year, month, day, intro, logourl, rate, actors





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

    def get_tempbox(self):
        num = str(self._tree.xpath('//span[@class="detail-num"]/text()')[0])
        level = str(self._tree.xpath('//span[@class="detail-unit"]/text()')[0]).strip()
        if level == '亿':
            num = str(float(num)*10000)
        return num
        # try:
        #     return str(self._tree.xpath('/html/body/div[2]/section[1]/div[1]/div[3]/a/div/div[3]/div[1]/p[2]/span[1]/text()')[0])
        # except:
        #     return str(self._tree.xpath('/html/body/div[2]/section[1]/div[1]/div[3]/a[1]/div/div[3]/div[1]/p[2]/span[1]/text()')[0])





class ExtensionParser:
    """
    parse extended information
    '//p[@class="title ellipsis-1"]/text()'
    """
    def __init__(self, Jsonin):
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

    def get_majoractors(self):
        text = self._dic['sectionHTMLs']['celebritySection']['html']
        try:
            tree = html.fromstring(text)
            actor_urls = tree.xpath('//div[@class="items"]//a/@href')[1:]
            actors = []
            for x in actor_urls:
                x = str(x).split('/')[2]
                actors.append(x)
        except:
            return []
        else:
            return actors
