# transcode
背景介绍： 语音识别需要一定的音频编码要求，根据不同编码有不同的解码方式，当数据量大或者不同编码混合时人为解码会存在困难。

设计思路： 通过获取音频编码信息，自动匹配不同解码方式。

解决问题点： 自动进行音频检测和自动解码，可进行大数据量的操作和统计音频时长。

*仅展示部分code
