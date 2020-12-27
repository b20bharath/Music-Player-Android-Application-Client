// AIDLFunServer.aidl
package com.example.funserveraidl;

// Declare any non-default types here with import statements

interface AIDLFunServer {
Bitmap getImage(int number);
void playSong(int number);
void pauseSong(int number);
void resumeSong(int number);
void stopSong(int number);
void stopServ();
}
