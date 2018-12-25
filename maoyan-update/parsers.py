import base64
from fontTools.ttLib import TTFont
from lxml import html
import re
import extend_crawler as Detail
from Pipeline import MoviePipeline

class ParseError(Exception):
    pass

class ParseList:
    @staticmethod
    def parse(box_office_doc):
        """解析猫眼票房页面，获取各正在上映影片的累计票房。
        :param box_office_doc: 页面内容。
        :return:总演员表（演员id）
        :raise: ParseError if parse html doc failed
        """
        tree = html.fromstring(box_office_doc)

        #获取字体
        font_face_style = tree.xpath('//style[@id="js-nuwa"]/text()')[0]
        try:
            font_face = re.match(r'.*base64,(.*)\) format.*', font_face_style, re.S).group(1)
        except AttributeError:
            raise ParseError('parse font-face failed')

        # 破解字体
        real_numbers = ParseList._parse_font_face(font_face)

        #获取新url
        urls = tree.xpath('//ul[@class="canTouch"]/@data-com')
        movies_urls = []
        for x in urls:
            movies_urls.append("https://piaofang.maoyan.com"+x.split("'")[1])

        #演员
        allactors = []

        i = 0
        for newurl in movies_urls:
            detail_crawler = Detail.MaoyanDetailCrawler(newurl)
            print(i)
            id, name, director, box_t, movie_class, year, month, day, intro, logourl, rate, actors = detail_crawler.crawl()
            # box = utils.multiple_replace(box_t, real_numbers)
            box = box_t
            i += 1
            allactors.extend(actors)
            print(actors)

            if id=='-1':
                continue

            movie = [i, name, director, box, movie_class, year, month, day, intro, logourl, actors, rate]
            pipe = MoviePipeline(movie)
            pipe.toserver()

        finalallactors = list(set(allactors))
        return finalallactors   #提取演员列表，交给上一级处理

    @staticmethod
    def _parse_font_face(font_face):
        """解析unicode值和十进制数字的映射。
        猫眼使用font_face进行反爬虫，html中的数字都是类似'&#xe4f9'这样的字符，这些字符和数字的映射被定义在字体文件中，
        经过渲染会显示为正常的数字。
        有两种方式应对这种反爬虫方式：1.解析字体文件，分析出映射关系；2.对网页截图，进行OCR。 这里使用第一种方法。
        字体文件被定义在页面上，是一串base64编码的字符串。对其解码后使用fontTools工具解析，获取glyph order，即是映射关系。
        :param font_face: 编码后的字体文件字符串
        :return: dict of unicode and number string
        """
        font_data = base64.b64decode(font_face)
        baselist=['.', '0', '4', '1', '5', '8', '7', '6', '9', '3', '2']
        baseUniCode = ['x', 'uniEE04', 'uniF4DE', 'uniF4B4', 'uniF617', 'uniE380', 'uniEC71',
                       'uniF526', 'uniE826', 'uniE148', 'uniE8B6']
        baseFont = TTFont('maoyan.woff')
        baseFont.saveXML('x.xml')
        numList=[]

        with open('maoyant.woff','w+b') as fp:
            fp.write(font_data)
            fp.seek(0)
            font = TTFont(fp.name)
            font.saveXML('y.xml')

            uniList = font['cmap'].tables[0].ttFont.getGlyphOrder()
            maoyanGlyph_temp = font['glyf']
            base_temp = baseFont['glyf']


            for i in range(1, 12):
                maoyanGlyph = maoyanGlyph_temp[uniList[i]]
                for j in range(11):
                    baseGlyph = base_temp[baseUniCode[j]]
                    if maoyanGlyph == baseGlyph:
                        numList.append(baselist[j])
                        break



            # getGlyphOrder()返回这样的列表：['glyph00000', 'x', 'uniEFD3', 'uniEC6A', 'uniE4F9', 'uniF8F3', 'uniF324',
            #  'uniE7F7', 'uniE711', 'uniF1C9', 'uniE21D', 'uniF1D7']
            #
            # 除去前两个元素，索引和元素值既是我们需要的映射关系，第三个元素对应0，第四个元素对应1...
            # 将其转换为{'\uefd3': '0', '\uec6a': '1', '\ue4f9': '2', '\uf8f3': '3', '\uf324': '4', '\ue7f7': '5',
            #  '\ue711': '6','\uf1c9': '7', '\ue21d': '8', '\uf1d7': '9'}这样的字典return出去
        return {eval("u\"" + '\\u' + uniList[i+1].split('uni')[-1].lower() + "\""): str(numList[i]) for i in range(1, 11)}



class ParserActor:
    """
    解析演员详情页面
    """
    @staticmethod
    def parse(page, actorid):
        """
        解析页面，直接输出
        :param page: 下载后的页面字符串
        :return: 无
        """
        tree = html.fromstring(page)

        #爬取id, movieid, name三个必有属性
        id = actorid
        # movieid = tree.xpath('//div[@class="p-link"]/@data-id')
        try:
            name = str(tree.xpath('//title/text()')[0])
        except:
            name = str(tree.xpath('//div[@class="string-ellipsis"]//span/text()')[0])

        name = name.strip()
        print(name)

        #爬去出生日期这个可能缺失的属性
        try:
            date = tree.xpath('//div[@data-id="birthday"]/span[2]/text()')[0]
        except:
            year=' '
            month=' '
            day=' '
        else:
            date = date[0:10]
            try:
                year = date.split('-')[0]
                month = date.split('-')[1]
                day = date.split('-')[2]
            except:
                month = date.split('-')[0]
                day = date.split('-')[1]
                year = ' '

        try:
            imgsrc = str(tree.xpath('//img/@src')[1]).strip()
            point = imgsrc.index('282w')
            imgsrc = imgsrc[0:point-1]
            #imgsrc = 'http:' + imgsrc
        except:
            imgsrc = ' '

        #获取性别：
        try:
            potential_content = tree.xpath('//p[@class="content-page"]//span[@class=" "]//span[@class="name"]/text()')
            print(potential_content)
        except:
            sex = '不明'
        else:
            i = 0
            sex = '不明'
            for label in potential_content:

                if label == '性别：':
                    sex = str(tree.xpath('//p[@class="content-page"]//span[@class=" "]//span[position()=2]/text()')[i]).strip()
                i += 1

        actor = [' ', name, year, month, day, imgsrc, sex, id]
        print(actor)
        return actor
