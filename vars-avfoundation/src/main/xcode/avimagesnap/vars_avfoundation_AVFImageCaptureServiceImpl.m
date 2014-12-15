#include "vars_avfoundation_AVFImageCaptureServiceImpl.h"
#include "AVFStillImageCapture.h"
#import <JavaNativeFoundation/JavaNativeFoundation.h>


AVFStillImageCapture *imageCapture = nil;

void initImageCapture() {
    if (imageCapture == nil) {
        imageCapture = [[AVFStillImageCapture alloc] init];
    }
}

/*
 * Class:     vars_avfoundation_AVFImageCaptureServiceImpl
 * Method:    videoDevicesAsStrings
 * Signature: ()[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_vars_avfoundation_AVFImageCaptureServiceImpl_videoDevicesAsStrings
(JNIEnv *env, jobject clazz) {
    
    initImageCapture();
	
	// Grab the array of NSStrings
	NSArray *videoDevicesAsStrings = [imageCapture videoCaptureDevicesAsStrings];
	
	// Look for the class for Java String
	jclass stringClass = (*env)->FindClass(env, "Ljava/lang/String;");
	
	// The array to return
	jobjectArray result = (*env)->NewObjectArray(env, [videoDevicesAsStrings count], stringClass, NULL);
	
	// Loop over devices
	for (int i = 0; i < [videoDevicesAsStrings count]; i++) {
		// Set the object in the java array to the device name converted to java string
		(*env)->SetObjectArrayElement(env, result, i, JNFNSToJavaString(env, [videoDevicesAsStrings objectAtIndex:i]));
	}
	
	// Return the result
	return result;

};

/*
 * Class:     vars_avfoundation_AVFImageCaptureServiceImpl
 * Method:    startSessionWithNamedDevice
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_vars_avfoundation_AVFImageCaptureServiceImpl_startSessionWithNamedDevice
(JNIEnv *env, jobject clazz, jstring namedDevice) {
	
	// Convert the incoming Device Name to an NSString
	NSString *device = JNFJavaToNSString(env, namedDevice);
    
    initImageCapture();

    [imageCapture setupCaptureSessionUsingNamedDevice: device];
	
	// Convert the filename back to jstring for return
	return JNFNSToJavaString(env, device);
	
};

/*
 * Class:     vars_avfoundation_AVFImageCaptureServiceImpl
 * Method:    saveSnapshotToSpecifiedPath
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_vars_avfoundation_AVFImageCaptureServiceImpl_saveSnapshotToSpecifiedPath
(JNIEnv *env, jobject clazz, jstring specifiedPath) {
	
	// Convert the incoming filename to an NSString
	NSString *path = JNFJavaToNSString(env, specifiedPath);
	
    if (imageCapture != nil) {
        [imageCapture saveStillImageToPath:path];
    }
    else {
        NSLog(@"Still image capture has not been configured. Unable to save %@", path);
    }
	
	// Convert the filename back to jstring for return
	return JNFNSToJavaString(env, path);

};

/*
 * Class:     vars_avfoundation_AVFImageCaptureServiceImpl
 * Method:    stopSession
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_vars_avfoundation_AVFImageCaptureServiceImpl_stopSession
(JNIEnv *env, jobject clazz) {
	
	// [imageCapture dealloc]; // Don't need. This project is using ARC
    imageCapture = nil; // On deallocation the session will be terminated
};


