# Baking App

![baking-1](https://user-images.githubusercontent.com/30549956/45268689-2ca3d180-b435-11e8-9d51-9ee74ab0a50e.jpg)

## Project Overview

This app will allow Udacity’s resident baker-in-chief, Miriam, to share her recipes with the world.
A user can select a recipe and see video-guided steps for how to complete it.

## Used Libraries

* [ButterKnife](https://github.com/JakeWharton/butterknife)
* [Gson](https://github.com/google/gson)
* [Picasso](https://github.com/square/picasso)
* [Timber](https://github.com/JakeWharton/timber)
* [ExoPlayer](https://github.com/google/ExoPlayer)
* [Stetho](https://github.com/facebook/stetho)

## Learning outcomes

* Use MediaPlayer/Exoplayer to display videos.
* Handle error cases in Android.
* Add a widget to the app experience.
* Leverage a third-party library in the app.
* Use Fragments to create a responsive design that works on phones and tablets.

## Rubric

### General App Usage
- [x] App should display recipes from provided network resource.
- [x] App should allow navigation between individual recipes and recipe steps.
- [x] App uses RecyclerView and can handle recipe steps that include videos or images.
- [x] App conforms to common standards found in the Android Nanodegree General Project Guidelines.

### Components and Libraries
- [x] Application uses Master Detail Flow to display recipe steps and navigation between them.
- [x] Application uses Exoplayer to display videos.
- [x] Application properly initializes and releases video assets when appropriate.
- [x] Application should properly retrieve media assets from the provided network links. It should properly handle network requests.
- [x] Application makes use of Espresso to test aspects of the UI.
- [x] Application sensibly utilizes a third-party library to enhance the app's features. That could be helper library to interface with Content Providers if you choose to store the recipes, a UI binding library to avoid writing findViewById a bunch of times, or something similar.

### Homescreen Widget
- [x] Application has a companion homescreen widget.
- [x] Widget displays ingredient list for desired recipe.
