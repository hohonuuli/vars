#include "vars_avfoundation_AVFImageCaptureServiceImpl.h"
#include "AVFStillImageCapture.h"
#import <JavaNativeFoundation/JavaNativeFoundation.h>


AVFStillImageCapture *imageCapture = nil;

/*
 * Class:     vars_avfoundation_AVFImageCaptureServiceImpl
 * Method:    videoDevicesAsStrings
 * Signature: ()[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_vars_avfoundation_AVFImageCaptureServiceImpl_videoDevicesAsStrings
(JNIEnv *env, jobject clazz) {
    // Set up an autoreleasePool for the JNI method - ALWAYS INCLUDE
	/*DON'T FORGET THIS LINE!!!*/
	JNF_COCOA_ENTER(env);
	
	// Grab the array of NSStrings
	NSArray *videoDevicesAsStrings = [AVFStillImageCapture videoCaptureDevicesAsStrings];
	
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
	
	// Clean up the autoreleasePool for the JNI method - ALWAYS INCLUDE
	/*DON'T FORGET THIS LINE AND A RETURN!!!*/
	JNF_COCOA_EXIT(env);
	return 0;
};

/*
 * Class:     vars_avfoundation_AVFImageCaptureServiceImpl
 * Method:    startSessionWithNamedDevice
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_vars_avfoundation_AVFImageCaptureServiceImpl_startSessionWithNamedDevice
(JNIEnv *env, jobject clazz, jstring namedDevice) {
    // Set up an autoreleasePool for the JNI method - ALWAYS INCLUDE
	/*DON'T FORGET THIS LINE!!!*/
	JNF_COCOA_ENTER(env);
	
	// Convert the incoming Device Name to an NSString
	NSString *device = JNFJavaToNSString(env, namedDevice);
    
    if (imageCapture == nil) {
        imageCapture = [[AVFStillImageCapture alloc] init];
    }

    [imageCapture setupCaptureSessionUsingNamedDevice: device];
	
	// Convert the filename back to jstring for return
	return JNFNSToJavaString(env, device);
	
	// Clean up the autoreleasePool for the JNI method - ALWAYS INCLUDE
	/*DON'T FORGET THIS LINE AND A RETURN!!!*/
	JNF_COCOA_EXIT(env);
	return 0;
};

/*
 * Class:     vars_avfoundation_AVFImageCaptureServiceImpl
 * Method:    saveSnapshotToSpecifiedPath
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_vars_avfoundation_AVFImageCaptureServiceImpl_saveSnapshotToSpecifiedPath
(JNIEnv *env, jobject clazz, jstring specifiedPath) {
    // Set up an autoreleasePool for the JNI method - ALWAYS INCLUDE
	/*DON'T FORGET THIS LINE!!!*/
	JNF_COCOA_ENTER(env);
	
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
	
	// Clean up the autoreleasePool for the JNI method - ALWAYS INCLUDE
	/*DON'T FORGET THIS LINE AND A RETURN!!!*/
	JNF_COCOA_EXIT(env);
	return 0;
};

/*
 * Class:     vars_avfoundation_AVFImageCaptureServiceImpl
 * Method:    stopSession
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_vars_avfoundation_AVFImageCaptureServiceImpl_stopSession
(JNIEnv *env, jobject clazz) {
    JNF_COCOA_ENTER(env);
	
	// [imageCapture dealloc]; // Don't need. This project is using ARC
    imageCapture = nil; // On deallocation the session will be terminated
	
	JNF_COCOA_EXIT(env);
};


