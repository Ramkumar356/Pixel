Pexel
=====

A Gallery application listing free stock photos from Pexel(https://www.pexels.com/). Pexel supports
fetching, decoding, and caching images efficiently using BitmapPool.

ScreenShot
--------
<img alt="App image" src="screenshots/scrolling.gif" >

Memory Management
-----------------

Loading bimaps into memory frequently results in very frequent calling of GC(Garbage Collector) which freeze UI thread. So to avoid such scenario Pexel uses Bitmappool to avoid reduce GC overhead that will result in smooth running application.

DownSampling
------------
Images are downsampled to view size before loading into memory irrespective of the original size.

