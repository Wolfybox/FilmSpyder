var app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    film: {},
    director: "",
    actors: [],
    collected: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    var that = this
    // 获取详情
    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/moviebyid.do',
      header: {
        "content-type": "application/json; charset=utf-8"
      },
      data: {
        id: options.id,
      },
      method: "POST",
      success: function(res) {
        console.log("status Code:" + res.statusCode)
        var jsonData = res.data
        console.log("返回数据:")
        console.log(jsonData)
        // var actorList = jsonData.actor
        // console.log("演员名单")
        // console.log(actorList)
        that.setData({
          film: jsonData
        })

        // 再次发送请求获取收藏
        wx.request({
          url: 'https://filmspyder.cn/MoviesServer/returncollect.do',
          header: {
            "content-type": "application/json; charset=utf-8"
          },
          data: {
            userid: app.globalData.uid
          },
          method: "POST",
          success: function(res) {
            console.log("status Code:" + res.statusCode)
            var jsonData = res.data
            console.log("返回收藏:")
            console.log(jsonData)
            // 查重
            const curId = that.data.film.id
            for (var i = 0; i < jsonData.length; i++) {
              var curItem = jsonData[i]
              if (curItem.id == curId) {
                that.setData({
                  collected: true
                })
              }
            }
          }
        })
      }
    })
  },


  onActorClick: function(e) {
    var name = e.currentTarget.dataset.name
    console.log(name)
    wx.navigateTo({
      url: '/pages/bio/bio?name=' + name,
    })
  },

  onStatsTap: function() {
    wx.showModal({
      title: '跳转提示',
      content: '即将跳转到数据页',
      success: function(res) {
        if (res.confirm) {
          wx.navigateTo({
            url: '/pages/stats/stats',
          })
        }
      }
    })
  },

  onCollectTap: function() {
    wx.showLoading({
      title: '收藏中',
    })
    var that = this
    // 翻转按钮状态
    that.setData({
      collected: !that.data.collected
    })
    if (that.data.collected) {
      wx.request({
        url: 'https://filmspyder.cn/MoviesServer/collect.do',
        header: {
          "content-type": "application/json; charset=utf-8"
        },
        data: {
          userid: app.globalData.uid,
          movieid: that.data.film.id
        },
        method: "POST",
        success: function(res) {
          console.log("status Code:" + res.statusCode)
          var jsonData = res.data
          console.log("返回数据:")
          console.log(jsonData)
          wx.hideLoading()
          wx.showToast({
            title: '收藏成功',
          })
        }
      })
    } else {
      wx.request({
        url: 'https://filmspyder.cn/MoviesServer/deletecollect.do',
        header: {
          "content-type": "application/json; charset=utf-8"
        },
        data: {
          userid: app.globalData.uid,
          movieid: that.data.film.id
        },
        method: "POST",
        success: function(res) {
          console.log("status Code:" + res.statusCode)
          var jsonData = res.data
          console.log("返回数据:")
          console.log(jsonData)
          wx.hideLoading()
          wx.showToast({
            title: '已取消',
          })
        }
      })

    }
  },

  onShareTap: function() {
    wx.showShareMenu({
      withShareTicket: true,
      success: function() {
        wx.showToast({
          title: '请点击右上角',
        })
      },
      fail: function() {
        wx.showToast({
          title: '分享失败',
        })
      }
    })
  },

  onShareAppMessage: function() {

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