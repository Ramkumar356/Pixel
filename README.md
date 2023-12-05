Pexel
=====

An Android application listing free stock photos from Pexel(https://www.pexels.com/) without using third party image loader by
fetching, decoding, and caching images efficiently using a custom BitmapPool.

ScreenShot
--------
<img alt="App image" src="screenshots/scrolling.gif" >

Efficent Memory Usage
-----------------

As memory(RAM) is a critical resource in mobile devices, loading bimaps into memory for every new image fills up the memory very quickly and results in UI freeze because of frequent G. Hence to avoid such scenario this application uses Bitmapool which reuses the memory of images that are out of display.



