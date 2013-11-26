Nov 22 15:52:55 zen.local avimagesnap[54222] <Error>: CGBitmapContextCreateImage: invalid context 0x0. This is a serious error. This application, or a library it uses, is using an invalid context  and is thereby contributing to an overall degradation of system stability and reliability. This notice is a courtesy: please fix this problem. It will become a fatal error in an upcoming update.

Nov 22 15:53:05 zen.local avimagesnap[54222] <Error>: ImageIO: CGImageDestinationAddImage image parameter is nil
Program ended with exit code: 9

Printing description of imageDataSampleBuffer:
CMSampleBuffer 0x10042ef80 retainCount: 1 allocator: 0x7fff7bbc6eb0
    invalid = NO
    dataReady = YES
    makeDataReadyCallback = 0x0
    makeDataReadyRefcon = 0x0
    buffer-level attachments:
        com.apple.cmio.buffer_attachment.discontinuity_flags(P) = 24584
        com.apple.cmio.buffer_attachment.hosttime(P) = 166219507059268
        com.apple.cmio.buffer_attachment.sequence_number(P) = 0
    formatDescription = <CMVideoFormatDescription 0x10042ee40 [0x7fff7bbc6eb0]> {
    mediaType:'vide' 
    mediaSubType:'jpeg' 
    mediaSpecific: {
        codecType: 'jpeg'       dimensions: 1280 x 720 
    } 
    extensions: {<CFBasicHash 0x10042ee70 [0x7fff7bbc6eb0]>{type = immutable dict, count = 5,
entries =>
    1 : <CFString 0x7fff7c128520 [0x7fff7bbc6eb0]>{contents = "Version"} = <CFNumber 0x127 [0x7fff7bbc6eb0]>{value = +1, type = kCFNumberSInt32Type}
    2 : <CFString 0x7fff7c128540 [0x7fff7bbc6eb0]>{contents = "RevisionLevel"} = <CFNumber 0x127 [0x7fff7bbc6eb0]>{value = +1, type = kCFNumberSInt32Type}
    3 : <CFString 0x7fff7ac80de8 [0x7fff7bbc6eb0]>{contents = "CVFieldCount"} = <CFNumber 0x127 [0x7fff7bbc6eb0]>{value = +1, type = kCFNumberSInt32Type}
    4 : <CFString 0x7fff7c128380 [0x7fff7bbc6eb0]>{contents = "FormatName"} = <CFString 0x7fff7cbd80e0 [0x7fff7bbc6eb0]>{contents = "Photo - JPEG"}
    5 : <CFString 0x7fff7c128560 [0x7fff7bbc6eb0]>{contents = "Vendor"} = <CFString 0x7fff7c128580 [0x7fff7bbc6eb0]>{contents = "appl"}
}
}
}
    sbufToTrackReadiness = 0x0
    numSamples = 1
    sampleTimingArray[1] = {
        {PTS = {166219507/1000 = 166219.507, rounded}, DTS = {INVALID}, duration = {33/1000 = 0.033}},
    }
    sampleSizeArray[1] = {
        sampleSize = 38854,
    }
    dataBuffer = 0x10042eef0