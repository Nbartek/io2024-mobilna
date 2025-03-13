# Projekt-io2024
Project made for a course titled: Software Engineering during 2nd year of Computer Science in Enginnering at AGH UST
---
Small scale postal It solution for sending parcels, administrating the postal warehouse and it's workers

# 3 main parts
- Kotlin/Androdid Studio, mobile app to symulate barcode zebra scanner utilites
- Desktop for office admins to administrate the workers
- Basic WWW site for sending a parcel at local postal place
- Database set in Azure with Transact-SQL

---
## All comments and most commits were made in polish
 This part(mobile app) includes:
 - login system
 - Qr code scanner(from api)
 - Some basic operations on database
 - Simple ui/ux interface

### Considerations (to explain some controversial aspects of this project)
 - Because of an error when using microsoft azure drivers for java i had to improvise and use the outdated JDBC drivers. That is the reason that i do not use any api's for database connection, and why my sql code is hardcoded.
 - The architecture is Client - Database adn it may seem uncommon, but there was some thought to it. The idea for it was that, because this app is meant to be used in local, hermetic environment, it should trive to achive best connection speed. In one warehouse there should be about 100 used scanners at one moment. Creating middle-man would clog the server side and made app feel sluggish
 - We use qr code, not barcodes, just a preference

#### Api's used:
CameraX https://developer.android.com/jetpack/androidx/releases/camera
Qrcode https://github.com/zxing/zxing
Database JTDS Drivers https://sourceforge.net/projects/jtds/ not recommended (only if you'll get handshake error with azure)
