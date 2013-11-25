//
//  main.c
//  avimagesnap
//
//  Created by Brian Schlining on 11/20/13.
//  Copyright (c) 2013 Brian Schlining. All rights reserved.
//

#include <stdio.h>
#include "AVFStillImageCapture.h"

int main(int argc, const char * argv[]) {
    
    //NSApplicationLoad(); // Is this needed?
    NSArray *deviceNames = [AVFStillImageCapture videoCaptureDevicesAsStrings];
    AVFStillImageCapture *imageCapture = [[AVFStillImageCapture alloc] init];
    [imageCapture setupCaptureSessionUsingNamedDevice:[deviceNames objectAtIndex:0]];
    for (int i = 0; i < 5; i++) {
        [imageCapture saveStillImageToPath:[NSString stringWithFormat:@"snapshot%d.png", i]];
        [NSThread sleepForTimeInterval:1.0f];
    }

    printf("Image capture completed\n");
    return 0;
}

