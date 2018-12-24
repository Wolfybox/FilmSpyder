const date = new Date()
const years = []
const seasons = []
const months = []

for (let i = 2012; i <= date.getFullYear(); i++) {
  years.push(i)
}

for (let i = 1; i <= 12; i++) {
  months.push(i)
}

const wxCharts = require("../../utils/wxcharts.js") // 导入chart库
const drawDiagram = require("../../utils/drawDiagram.js")
Page({

  /**
   * 页面的初始数据
   */
  data: {
    years: years,
    year: 2015,
    seasons: ["第一", "第二", "第三", "第四"],
    season: "第四",
    months: months,
    month: 4,
    value: [3, 3, 3],
    pieId: "pieCanvas",
    ringId: "ringCanvas",
    dataBySeason: {},
    dataByMonth: {},
    windowWidth: 0,
    chartShown: false,
    yearText: "",
    seasonText: "",
    monthText: ""
  },



  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    let windowWidth = 320;
    try {
      let res = wx.getSystemInfoSync();
      windowWidth = res.windowWidth;
      this.setData({
        windowWidth: windowWidth
      })
    } catch (e) {
      // do something when get system info failed
    }
    // var low = 0x00
    // var high = 0x0f
    // var part = (high + 13) / 2
    // console.log(part)
  },

  onGenTap: function() {
    wx.showLoading({
      title: '生成中',
    })
    this.getCharts()
    wx.hideLoading()
  },

  bindChange: function(e) {
    const val = e.detail.value
    this.setData({
      year: this.data.years[val[0]],
      season: this.data.seasons[val[1]],
      month: this.data.months[val[2]],
    })
    console.log(val)
  },

  // 生成图表
  getCharts: function() {
    var year = this.data.year
    year = year + ""
    var month = this.data.month
    // 位数补齐
    if (month < 10) {
      month = "0" + month
    } else {
      month = "" + month
    }
    var that = this
    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/statisticsbyquarter.do',
      header: {
        "content-type": "application/json; charset=utf-8"
      },
      data: {
        year: year,
        quarter: that.data.season + "季度"
      },
      method: "POST",
      success: function(res) {
        console.log("status Code:" + res.statusCode)
        var jsonData = res.data
        console.log("返回数据:")
        console.log(jsonData)
        that.setData({
          dataBySeason: jsonData
        })
        that.buildPie()
      }
    })
    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/statisticsbymonth.do',
      header: {
        "content-type": "application/json; charset=utf-8"
      },
      data: {
        year: year,
        month: month
      },
      method: "POST",
      success: function(res) {
        console.log("status Code:" + res.statusCode)
        var jsonData = res.data
        console.log("返回数据:")
        console.log(jsonData)
        that.setData({
          dataByMonth: jsonData
        })
        that.buildRing()
      }
    })
  },

  // 生成饼图
  buildPie: function() {
    var data = this.data.dataBySeason
    var dataSet = []
    for (var key in data) {
      console.log("key:" + key + "data:" + data.key)
      dataSet.push({
        name: key,
        data: data[key]
      })
    }
    console.log("重构数据：")
    console.log(dataSet)
    dataSet = drawDiagram.addColor(dataSet)
    var that = this
    new wxCharts({
      canvasId: that.data.pieId,
      type: 'pie',
      series: dataSet,
      width: that.data.windowWidth,
      height: 200,
      dataLabel: false
    });
    that.setData({
      chartShown: true,
      yearText: that.data.year + "",
      seasonText: that.data.season,
    })
  },


  // 生成环图
  buildRing: function() {
    var data = this.data.dataByMonth
    var dataSet = []
    for (var key in data) {
      console.log("key:" + key + "data:" + data.key)
      dataSet.push({
        name: key,
        data: data[key]
      })
    }
    console.log("重构数据：")
    console.log(dataSet)
    dataSet = drawDiagram.addColor(dataSet)
    var that = this
    new wxCharts({
      canvasId: that.data.ringId,
      type: 'ring',
      series: dataSet,
      width: that.data.windowWidth,
      height: 200,
      dataLabel: false
    });
    that.setData({
      chartShown: true,
      yearText: that.data.year + "",
      monthText: that.data.month + ""
    })
  },


  longPressPie: function() {
    var that = this
    drawDiagram.saveChart(that.data.pieId)
  },

  longPressRing: function() {
    var that = this
    drawDiagram.saveChart(that.data.ringId)
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