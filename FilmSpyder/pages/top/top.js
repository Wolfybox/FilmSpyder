const date = new Date()
const years = []
const tops = []

for (let i = 2012; i <= date.getFullYear(); i++) {
  years.push(i)
}

for (let i = 3; i <= 15; i++) {
  tops.push(i)
}

const wxCharts = require("../../utils/wxcharts.js") // 导入chart库
const drawDiagram = require("../../utils/drawDiagram.js")
Page({

  /**
   * 页面的初始数据
   */
  data: {

    years: years,
    year: 2018,
    tops: tops,
    top: 9,
    value: [6],
    canvasId: "columnCanvas",
    topData: {},
    windowWidth: 0
  },

  bindYearChange: function(e) {
    const val = e.detail.value
    this.setData({
      year: this.data.years[val[0]]
    })
    console.log("yearChange" + this.data.year)
  },

  bindTopChange: function(e) {
    const val = e.detail.value
    this.setData({
      top: this.data.tops[val[0]],
    })
    console.log("TOPChange：" + this.data.top)
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    let windowWidth = 320;
    try {
      let res = wx.getSystemInfoSync();
      windowWidth = res.windowWidth;
    } catch (e) {
      // do something when get system info failed
    }
    this.setData({
      windowWidth: windowWidth
    })

  },

  // 生成图表
  getCharts: function() {
    var year = this.data.year + ""
    var top = this.data.top
    var that = this
    console.log("年份：" + year + "/TOP：" + top)
    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/statisticstop.do',
      header: {
        "content-type": "application/json; charset=utf-8"
      },
      data: {
        year: year,
        topnumber: top
      },
      method: "POST",
      success: function(res) {
        console.log("status Code:" + res.statusCode)
        var jsonData = res.data
        console.log("Top返回数据:")
        console.log(jsonData)
        that.setData({
          topData: jsonData
        })
        that.buildColumn()
        wx.hideLoading()
      },
      fail: function() {
        console.log("Top数据获取失败")
        wx.hideLoading()
      }
    })
  },

  // 生成键
  onGenTap: function() {
    wx.showLoading({
      title: '生成中',
    })
    this.getCharts()
  },

  // 绘制柱状图
  buildColumn: function() {
    var that = this
    var year = that.data.year
    var dataTop = that.data.topData
    var filmNames = []
    var stocks = []
    for (var key in dataTop) {
      filmNames.push(key)
      stocks.push(dataTop[key] / 10000)
    }
    //加载页面时，调用wxCharts绘制图表
    new wxCharts({
      canvasId: that.data.canvasId,
      type: 'column',
      categories: filmNames,
      series: [{
        name: year + "年" + " TOP" + this.data.top + ' 票房',
        data: stocks
      }],
      yAxis: {
        format: function(val) {
          return val + '亿';
        }
      },
      width: that.data.windowWidth,
      height: 400
    });
  },

  longTapDiagrams: function() {
    var that = this
    drawDiagram.saveChart(that.data.canvasId)
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