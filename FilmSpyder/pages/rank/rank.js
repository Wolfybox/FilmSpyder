Page({

  /**
   * 页面的初始数据
   */
  data: {
    rankList: [],
    colds: [],
    topRate: [],
    topTen: [],
    sciFic: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    var that = this
    // 本月TOP10
    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/top10.do',
      header: {
        "content-type": "application/json; charset=utf-8"
      },
      data: {},
      method: "POST",
      success: function(res) {
        console.log("status Code:" + res.statusCode)
        var jsonData = res.data
        console.log("返回数据:")
        console.log(jsonData)
        // 取出前三电影
        var films = {}
        films["film1"] = jsonData[0]
        films["film2"] = jsonData[1]
        films["film3"] = jsonData[2]

        // 添加数据
        var rankList = that.data.rankList
        console.log(films)
        rankList.push({
          title: "本月TOP10",
          thumb: films.film1.thumburl,
          films: films
        })
        // 绑定数据
        that.setData({
          rankList: rankList,
          topTen: jsonData
        })
      }
    }) 

    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/cold10.do',
      header: {
        "content-type": "application/json; charset=utf-8"
      },
      data: {},
      method: "POST",
      success: function(res) {
        console.log("status Code:" + res.statusCode)
        var jsonData = res.data
        console.log("返回数据:")
        console.log(jsonData)
        // 取出前三电影
        var films = {}
        films["film1"] = jsonData[0]
        films["film2"] = jsonData[1]
        films["film3"] = jsonData[2]

        // 添加数据
        var rankList = that.data.rankList
        console.log(films)
        rankList.push({
          title: "冷门佳片",
          thumb: films.film1.thumburl,
          films: films
        })
        // 绑定数据
        that.setData({
          rankList: rankList,
          colds: jsonData
        })
      }
    })

    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/toprate10.do',
      header: {
        "content-type": "application/json; charset=utf-8"
      },
      data: {},
      method: "POST",
      success: function(res) {
        console.log("status Code:" + res.statusCode)
        var jsonData = res.data
        console.log("返回数据:")
        console.log(jsonData)
        // 取出前三电影
        var films = {}
        films["film1"] = jsonData[0]
        films["film2"] = jsonData[1]
        films["film3"] = jsonData[2]

        // 添加数据
        var rankList = that.data.rankList
        console.log(films)
        rankList.push({
          title: "评分最高",
          thumb: films.film1.thumburl,
          films: films
        })
        // 绑定数据
        that.setData({
          rankList: rankList,
          topRate: jsonData
        })
      }
    })

    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/science10.do',
      header: {
        "content-type": "application/json; charset=utf-8"
      },
      data: {},
      method: "POST",
      success: function(res) {
        console.log("status Code:" + res.statusCode)
        var jsonData = res.data
        console.log("返回数据:")
        console.log(jsonData)
        // 取出前三电影
        var films = {}
        films["film1"] = jsonData[0]
        films["film2"] = jsonData[1]
        films["film3"] = jsonData[2]

        // 添加数据
        var rankList = that.data.rankList
        console.log(films)
        rankList.push({
          title: "10大科幻好片",
          thumb: films.film1.thumburl,
          films: films
        })
        // 绑定数据
        that.setData({
          rankList: rankList,
          sciFic: jsonData
        })
      }
    })
  },

  onRankClick: function(e) {
    var title = e.currentTarget.dataset.name
    console.log(title)
    var filmList = []
    switch (title) {
      case "冷门佳片":
        filmList = this.data.colds
        break;
      case "评分最高":
        filmList = this.data.topRate
        break;
      case "本月TOP10":
        filmList = this.data.topTen
        break;
      case "10大科幻好片":
        filmList = this.data.sciFic
        break;
    }
    console.log(filmList)
    wx.navigateTo({
      url: '/pages/rankdetail/rankdetail?title=' + title + "&filmList=" + JSON.stringify(filmList)
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function() {

  }
})