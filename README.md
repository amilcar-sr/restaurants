# Restaurants

This application doesn't include the credentials for **clientId** nor **clientSecret**, you'll have to get those values from the assignment document and place them in the **BaseActivity#tokenObservable** (BaseActivity : 41) in order to make it work.

## Location permission
If the user provides permission to acces the device's location, the app will use that location as starting point. Otherwise, the app will use the point in the API document example (-34.90369, -56.19264).

## Pick a point for restaurant requests
All you have to do in order to change the point for the request is **long press on the desired coordinates in the map view**.

## Libraries used
- **Retrofit:** HTTP requests.
- **RxAndroid:** Manage background tasks and handle the loading flow.
- **Material:** UI componets such as the BottomNavigationView.
- **Google Maps:** Mapping experience.
- **ViewModel:** Hold information across the activity and fragments lifecycles.
- **LiveData:** Notify different components in the app about information updates.

