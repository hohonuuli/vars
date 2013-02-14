//
//  QTKitImageCapture.h
//  QTKitImageCapture
//
//  Created by Brian Schlining on 2010-01-08.  Edited by Karen Salamy (10/05/2010)
//  Based on the ImageSnap Open source code from http://iharder.sourceforge.net
//  ImageSnap.h
//  ImageSnap
//
//  Created by Robert Harder on 9/10/09.
//
#import <Cocoa/Cocoa.h>
#import <QTKit/QTKit.h>
#include "QTKitImageCapture.h"

#define error(...) fprintf(stderr, __VA_ARGS__)
#define console(...) (g_quiet ? 0 : printf(__VA_ARGS__))

BOOL g_verbose = NO;
BOOL g_quiet = NO;
NSString *VERSION = @"0.2.4";


@interface QTKitImageCapture : NSObject {
    
    QTCaptureSession                    *mCaptureSession;
    QTCaptureDeviceInput                *mCaptureDeviceInput;
    QTCaptureDecompressedVideoOutput    *mCaptureDecompressedVideoOutput;
    CVImageBufferRef                    mCurrentImageBuffer;
}


/**
 * Returns all attached QTCaptureDevice objects that have video.
 * This includes video-only devices (QTMediaTypeVideo) and
 * audio/video devices (QTMediaTypeMuxed).
 *
 * @return autoreleased array of video devices
 */
+(NSArray *)videoDevices;

+(NSArray *)videoDevicesAsStrings;

/**
 * Returns the default QTCaptureDevice object for video
 * or nil if none is found.
 */
+(QTCaptureDevice *)defaultVideoDevice;

/**
 * Returns the QTCaptureDevice with the given name
 * or nil if the device cannot be found.
 */
+(QTCaptureDevice *)deviceNamed:(NSString *)name;

/**
 * Writes an NSImage to disk, formatting it according
 * to the file extension. If path is "-" (a dash), then
 * an jpeg representation is written to standard out.
 */
+ (BOOL)saveImage:(NSImage *)image toPath: (NSString*)path;

/**
 * Converts an NSImage to raw NSData according to a given
 * format. A simple string search is performed for such
 * characters as jpeg, tiff, png, and so forth.
 */
+(NSData *)dataFrom:(NSImage *)image asType:(NSString *)format;


/**
 * Starts a Capture Session.
 * Activates the video source with a named device.
 */
+(BOOL)startSessionWithNamedDevice:(QTCaptureDevice *)device;
-(id)init;
-(void)dealloc;
-(BOOL)startSession:(QTCaptureDevice *)device;


/**
 * Captures a snapshot and saves it to a specific path.
 */
-(BOOL)saveSnapshotToSpecifiedPath:(NSString *)path;


/**
 * Stops the existing capture session.
 */
-(void)stopSession;


@end

