// ---------------生成时间---------------------
const date = new Date()
const years = []
const tops = []
for (let i = 2012; i <= date.getFullYear(); i++) {
  years.push(i)
}

for (let i = 3; i <= 20; i++) {
  tops.push(i)
}


const wxCharts = require("../../utils/wxcharts.js") // 导入chart库
const drawDiagram = require("../../utils/drawDiagram.js")
// ---------------绘制直方图-------------------
Page({
  data: {
    years: years,
    year: 2018,
    tops: tops,
    top: 9,
    value: [6],
    canvasId: "areaCanvas",
    chartShown: false,
    windowWidth: 0,
    topData: [],
    yearText: "",
    topText: ""
  },

  // -----------------日期数据监听------------------

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
      top: this.data.tops[val[0]]
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

  onGenTap: function() {
    wx.showLoading({
      title: '生成中',
    })
    this.getCharts()
  },

  // 生成图表
  getCharts: function() {
    var year = this.data.year + ""
    var top = this.data.top
    var that = this
    console.log("年份：" + year + "/TOP：" + top)
    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/actoryear.do',
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
        console.log("劳模返回数据:")
        console.log(jsonData)
        wx.hideLoading()
        that.setData({
          topData: jsonData,
          chartShown: true,
          yearText: year,
          topText: top
        })
        that.buildArea()
      },
      fail: function() {
        console.log("劳模数据获取失败")
        wx.hideLoading()
      }
    })
  },


  descending: function(x, y) {
    return y.num - x.num
  },

  // 绘制Area状图
  buildArea: function() {
    var that = this
    var year = that.data.year
    var dataTop = that.data.topData
    var maleList = dataTop.male
    var femaleList = dataTop.female
    // var maleNames = []
    var maleNums = []
    // var femaleNames = []
    var femaleNums = []
    var nameList = []
    for (var key in maleList) {
      var curMale = maleList[key]
      var curFemale = femaleList[key]
      nameList.push(curMale.key + "/" + curFemale.key)
      maleNums.push(curMale.value)
      femaleNums.push(curFemale.value)
    }
    // for (var keyMale in maleList) {
    //   maleNames.push(keyMale)
    //   maleNums.push(maleList[keyMale])
    // }
    // for (var keyFemale in femaleList) {
    //   femaleNames.push(keyFemale)
    //   femaleNums.push(femaleList[keyFemale])
    // }
    // for (var i = 0; i < maleNames.length; i++) {
    //   nameList.push(maleNames[i] + "/" + femaleNames[i])
    // }
    console.log(nameList)
    //绘制图表
    new wxCharts({
      canvasId: that.data.canvasId,
      type: 'area',
      categories: nameList,
      series: [{
        name: '男演员',
        data: maleNums,
        format: function(val) {
          return val.toFixed(0) + '部';
        }
      }, {
        name: '女演员',
        data: femaleNums,
        format: function(val) {
          return val.toFixed(0) + '部';
        }
      }],
      yAxis: {
        format: function(val) {
          return val.toFixed(0) + '部';
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