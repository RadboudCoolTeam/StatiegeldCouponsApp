# StatiegeldCouponsApp

This project made by Jacob Sykulski, Konrad Blachnio, Wiktor Kaliszewski and Mikhail Ushakov for the Research and Development course at Radboud University.

## Project description

This application helps you store and manage statiegeld coupons. 
The coupons are stored on a server and they are assigned to your personal account. 
The coupon scanner detects all the important information such as the barcode, the amount of money, and the supermarket name. 
Coupons can also be added manually. You can then store those coupons in folders and look at the pie chart to see how much money has been stored in all coupons. 
Every coupon is easily accessible by simply clicking on it, this will display the barcode that can be scanned at the checkout counter. 
All coupons can be filtered by supermarket name. It is also possible to set an avatar picture.    

## Installation guide

The application requires Android 5.0 or higher.

## How to use the project

After opening the app for the first time, the user is logged out. 
In order to log in or create a new account, the user should click on the profile picture (which is set to the default one) and then choose the option “Account”. 

<img src="/img/img1.png" alt="Screenshot 1" width=200>

Then the user is prompted to enter his login details. 

<img src="/img/img2.png" alt="Screenshot 1" width=200>

After logging in, the user’s account details are displayed. 

<img src="/img/img3.png" alt="Screenshot 1" width=200>

After clicking the “back arrow” the main screen is shown with the associated coupon information.

<img src="/img/img4.png" alt="Screenshot 1" width=200>

By clicking on one coupon, the coupon is displayed

<img src="/img/img5.png" alt="Screenshot 1" width=200>

By clicking on “+” and then on the shutter icon a new coupon can be added

<img src="/img/img6.png" alt="Screenshot 1" width=200>

When a user scrolls down the background picture reclines and the filter menu stays 

<img src="/img/img7.jpg" alt="Screenshot 1" width=200>

## How does the project work

As was mentioned in the introduction, our information system consists of 2 parts: 
a mobile app and a [web](https://github.com/RadboudCoolTeam/StatiegeldCouponsAPI) server. 

The mobile app allows users to gather coupon information, and maintain it in the app. Users can add, remove, and filter coupons. Also, our mobile app provides data persistence functionality - after restarting the app the coupons added in the app are remains accessible. Another important role of the application is the synchronization of data with the server: the user of the application has a chance to create an account in the app and store the coupons on the server.

The server helps to store the coupon and statistics data in the database and also provides an API with which the app interacts during the synchronization process.

Actually, this API is a connection between the server-side and the app.

How does the application work:

On the start-up, the app loads the user data from the local database (Room database).
Afterward, the app checks if the user is logged in to the app.
If there is a user record in the app database, the application tries to connect to the server and synchronize the coupon records.
Then the app loads the profile picture and draws the UI of the Home screen.

After this initial phase is finished, the user can interact with coupons: open them, delete them, and edit them. Also, users can add new coupons manually or by using a camera and AI text recognition. Besides that, if the user will open the avatar picture there will be accessible the menu with statistics. And possible to log in to a different account.

## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.
