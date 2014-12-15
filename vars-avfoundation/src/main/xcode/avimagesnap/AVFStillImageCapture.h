//
//  AVFStillImageCapture.h
//  avimagesnap
//
//  Created by Brian Schlining on 11/21/13.
//  Copyright (c) 2013 Brian Schlining. All rights reserved.
//

#import <AppKit/AppKit.h>
#import <AVFoundation/AVFoundation.h>
#import <CoreGraphics/CoreGraphics.h>
#import <CoreMedia/CoreMedia.h>
#import <CoreVideo/CoreVideo.h>
#import <Foundation/Foundation.h>
#import <ImageIO/ImageIO.h>

@interface AVFStillImageCapture : NSObject {}

@property (nonatomic, strong) AVCaptureSession *session;
@property (nonatomic, strong) AVCaptureStillImageOutput *stillImageOutput;
@property (nonatomic, strong) AVCaptureDeviceInput *videoInput;

-(NSArray *) videoCaptureDevices;
-(NSArray *) videoCaptureDevicesAsStrings;
-(AVCaptureDevice *) videoCaptureDeviceNamed: (NSString *)name;
-(void) initSession;
-(void) setupCaptureSessionUsingNamedDevice: (NSString *) name;
-(void) saveStillImageToPath: (NSString *) path;

@end
