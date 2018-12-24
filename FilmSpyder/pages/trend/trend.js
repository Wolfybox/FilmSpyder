const date = new Date()
const froms = []
const tos = []

for (let i = 2012; i <= date.getFullYear(); i++) {
  froms.push(i)
  tos.push(i)
}

const wxCharts = require("../../utils/wxcharts.js") // 导入chart库
const drawDiagram = require("../../utils/drawDiagram.js")
Page({

  /**
   * 页面的初始数据
   */
  data: {
    froms: froms,
    fromYear: 2012,
    tos: tos,
    toYear: 2012,
    value: [0],
    canvasId: "lineCanvas",
    dataByYear: [],
    windowWidth: 0,
    fromText: "",
    toText: "",
    chartShown: false
  },

  bindChangeFrom: function(e) {
    const val = e.detail.value
    this.setData({
      fromYear: this.data.froms[val[0]],
    })
  },

  bindChangeTo: function(e) {
    const val = e.detail.value
    this.setData({
      toYear: this.data.tos[val[0]],
    })
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
  },

  onGenTap: function() {
    wx.showLoading({
      title: '生成中',
    })
    this.getCharts()
  },

  // 生成图表
  getCharts: function() {
    var fromYear = this.data.fromYear + ""
    var toYear = this.data.toYear + ""
    var that = this
    console.log("起始年份：" + fromYear + "终止年份：" + toYear)
    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/statisticsyear.do',
      header: {
        "content-type": "application/json; charset=utf-8"
      },
      data: {
        startyear: fromYear,
        endyear: toYear
      },
      method: "POST",
      success: function(res) {
        console.log("status Code:" + res.statusCode)
        var jsonData = res.data
        console.log("返回数据:")
        console.log(jsonData)
        that.setData({
          dataByYear: jsonData,
          fromText: fromYear,
          toText: toYear,
          chartShown: true
        })
        that.buildLine()
        wx.hideLoading()
      },
      fail: function() {
        console.log("Trend数据获取失败")
      }
    })
  },

  // 生成折线图
  buildLine: function() {
    var year = this.data.dataByYear
    var dataSet = []
    for (var i = 0; i < year.length; i++) {
      // 按序获取每一个年份的数据
      var curYearData = year[i]
      // 去除序号保留所有月份数据
      var monthData = []
      console.log("curYearData:" + curYearData)
      for (var key in curYearData) {
        console.log("key:" + key + "value:" + curYearData[key])
        monthData.push(curYearData[key] / 10000)
      }
      // 加入数据
      dataSet.push({
        name: (this.data.fromYear + i) + "",
        data: monthData,
        format: function(val) {
          // return val.toFixed(2) + '亿'
        }
      })
    }
    console.log("重构数据：")
    console.log(dataSet)
    var that = this
    //加载页面时，调用wxCharts绘制图表
    new wxCharts({
      canvasId: 'lineCanvas',
      type: 'line',
      categories: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
      series: dataSet,
      yAxis: {
        title: '票房 (亿)',
        format: function(val) {
          return val.toFixed(2);
        },
        min: 0
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