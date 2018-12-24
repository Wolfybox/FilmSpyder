function saveChart(chartId) {
  // 查看是否授权
  wx.getSetting({
    success(res) {
      wx.showToast({
        title: '获取用户配置成功',
      })
      if (res.authSetting['scope.writePhotosAlbum']) {
        // 已经授权，直接开始保存
        contextDraw(chartId)
      } else {
        console.log("用户未预先授权")
        wx.showToast({
          title: '未预先授权',
        })
        wx.authorize({
          scope: 'scope.writePhotosAlbum',
          // 授权成功
          success() {
            console.log("用户已点击确认授权")
            wx.showToast({
              title: '授权成功',
            })
            contextDraw(chartId)
          },
          // 用户不授权
          fail() {
            console.log("用户拒绝授权")
            wx.showToast({
              title: 'FilmSpyder需要授权使用',
            })
          }
        })
      }
    },
    fail() {
      wx.showToast({
        title: '获取用户配置失败',
      })
    }
  })
}

function contextDraw(chartId) {
  wx.showLoading({
    title: '图片保存中',
  })
  const context = wx.createCanvasContext(chartId)
  // context.setFillStyle('red')
  // context.fillRect(20,20,150,200)
  context.draw(true, setTimeout(function() {
    wx.canvasToTempFilePath({
      canvasId: chartId,
      success: function(res) {
        console.log("临时存储地址：")
        console.log(res.tempFilePath)
        wx.saveImageToPhotosAlbum({
          filePath: res.tempFilePath,
          success: (res) => {
            console.log("图表保存成功")
            wx.showToast({
              title: '图表保存成功',
            })
            wx.hideLoading()
          },
          fail: (res) => {
            console.log("图表保存失败")
            wx.showToast({
              title: '图表保存失败',
            })
            wx.hideLoading()
          }
        })
      },
      fail: function(res) {
        console.log("临时存储失败")
        wx.showToast({
          title: '临时存储失败',
        })
        wx.hideLoading()
      }
    }, this)
  }, 200))
}

// 生成颜色
function generateColor(num) {
  var low = 0x000000
  var high = 0xff00ff
  var part = parseInt((high + 1) / num)
  var colorList = []
  for (var i = 0; i < num; i++) {
    colorList.push("#" + (i * part).toString(16))
  }
  console.log("生成颜色表" + colorList)
  return colorList
}

// 给数据集绑定颜色
function addColor(dataSet) {
  // var colorList = generateColor(dataSet.length)
  var colorList = [
    "#FFC312", "#C4E538", "#12CBC4", "#FDA7DF", "#ED4C67",
    "#F79F1F", "#A3CB38", "#1289A7", "#D980FA", "#B53471",
    "#EE5A24", "#009432", "#0652DD", "#9980FA", "#833471",
    "#EA2027", "#006266", "#1B1464", "#5758BB", "#6F1E51",
    "#B8860B", "#800000", "#696969", "#006400", "#F0FFF0"
  ]
  var index = 0
  for (var i in dataSet) {
    dataSet[i].color = colorList[index]
    index = index + 1
  }
  console.log("添加颜色:")
  console.log(dataSet)
  return dataSet
}


module.exports = {
  saveChart: saveChart,
  generateColor: generateColor,
  addColor: addColor
}