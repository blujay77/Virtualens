# Virtualens
### Screenshots
<p>
  <img src="https://github.com/user-attachments/assets/0b820f02-0f74-4870-9a07-8a4e08c989af" width=300>
  <img src="https://github.com/user-attachments/assets/b1e84164-a413-41af-8ef2-7bf1bab7a202" width=300>
  <img src="https://github.com/user-attachments/assets/26e223ee-8b11-4781-8c85-23e21c1419fc" width=300>
</p>


### Video Demo: https://youtu.be/C6rqYeVu__Y
### Description
Virtualens is an Android application that lets you apply various filters to an image sequentially. This allows not only for basic adjustments, but also the creation of entirely unique filters with only a few button presses! 
#### Loading images
Tap the large folder button to select an image from your phone's storage.
#### Adding filters
Below the folder button, you'll find a list with a single element: A button which will open a new menu. This menu lists all available filters and their respective categories. To add a filter, simply tap its name in the list! Now the list will be updated to show, what filter it is you added. Repeat this process as many times as needed.
#### Parameters
Many filters let you make some additional changes to their behavior. This is done by adjusting the parameters that appear below the filter's name in the list on the bottom half of your screen. To see what values you can insert, press the little info icon to the left of the parameters. Make sure your entered values are in the accepted range, otherwise your image will not be processed.
#### Saving images
Once your image is processed by clicking the button in the bottom right of your screen, you can save it to your device's pictures folder by pressing the save button.
#### Settings
You can adjust some quality settings to trade processing time and required system ressources for image quality. To do this, press the wrench symbol at the top of your screen.
##### Color depth
There, you can change the image's color depth to reduce the size of the image in your phone's memory. The options are to store each pixel's channels as an unsigned 8-bit integer, labelled as "Low" or a 32-bit float, labelled as "High". 

Increasing the brightness of a regular photo by 1000x will result in a white image with all detail erased. This is the same for both settings. However, the high color depth allows you to reduce the brightness again and end up where you started, while reducing the brightness of a low color depth image will result in the brightness of the previously created white image being reduced. This will result in gray pixels, instead of the original image. Keep in mind though, that this reversal only works if you apply the brightness changes in the same process, saving a clipped image will not allow you to restore lost detail!
##### Resolution
Very large images might use up too much memory for your system to handle, so this setting allows you to scale them down in order to keep the app running. This is done by capping the image dimensions to a maximum value, that loaded images cannot exceed. If they are wider or higher than your selected maximum, they are scaled down automatically to fit into the size you chose. While this will result in a decrease in image quality for large images, it ensures a smoother user experience.
#### Combining images
In addition to applying filters, you can also combine images!
##### Add/Subtract
Add or subtract two images' brightness values!
##### Multiply/Divide
The values for these operations are always set between 0 and 1. So multiplying will never result in a brighter image, but it can be used for masking when multiplying with a grayscale image. And in reverse, dividing will never decrease brightness, since you will always be dividing by a number smaller or equal to 1.
##### Max/Min
Pick only the brighter or darker value for each pixel!

### Structure
The actual code I wrote is distributed across 13 files, 10 of which are written in Kotlin, while the rest is in C++. 

For the UI, I used Android's Jetpack Compose, since it is the most modern and one of the easier approaches to native Android development. It is the main content of 5 of the Kotlin files (CombinationScreen.kt, FilterList.kt, MainScreen.kt, ResultScreen.kt, SettingsMenu.kt).

The three C++ files are only used for the filters themselves, since they rely heavily on OpenCV, which is made largely in C++ and also offers more freedom compared to Java/Kotlin. They can be used with the Android NDK which allows the use of native C++ code in a project that is otherwise written in Kotlin.

The other Kotlin files take care of managing and saving the UI State (MainActivityViewModel.kt, State.kt), implementing and applying the filters to the images (ImageFilter.kt, ImageProcessor.kt) or making sure that things actually appear on the user's screen (MainActivity.kt).
