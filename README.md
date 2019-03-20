# Skies
My First Android App. 
This app lets the user keep track of favourite satellites, plotting their path across the surface of the earth.

For the app to work:
1. You need a google maps API key which goes in the Skies/app/src/debug/res/values/google_maps_api.xml file.
2. You need an api key from the https://www.n2yo.com/api/ website. 
   Put this key in the strings resource file under the TODO comment Skies/app/src/main/res/values/strings.xml
   
Currently the app initialises its database from a CSV file in the raw folder under resources. Ideally it would get this data
from a web Api that can be kept up to date more easily.

N.B. 
The n2yo api key usually only allows 1000 api transactions per hour. When these are exceeded, the api will not return
any data.

Acknowledgements. 
I got the satellite Icon I used in the app from freepik.com

Icons made by <a href="https://www.freepik.com/" 
title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/"
title="Flaticon">www.flaticon.com</a> is licensed by
<a href="http://creativecommons.org/licenses/by/3.0/" 
title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a>


