package org.apache.tika.metadata;

import java.util.Date;

public interface XMPDM {
   Property ABS_PEAK_AUDIO_FILE_PATH = Property.internalURI("xmpDM:absPeakAudioFilePath");
   Property ALBUM = Property.externalText("xmpDM:album");
   Property ALT_TAPE_NAME = Property.externalText("xmpDM:altTapeName");
   Property ARTIST = Property.externalText("xmpDM:artist");
   Property ALBUM_ARTIST = Property.externalText("xmpDM:albumArtist");
   Property AUDIO_MOD_DATE = Property.internalDate("xmpDM:audioModDate");
   Property AUDIO_SAMPLE_RATE = Property.internalInteger("xmpDM:audioSampleRate");
   Property AUDIO_SAMPLE_TYPE = Property.internalClosedChoise("xmpDM:audioSampleType", "8Int", "16Int", "32Int", "32Float");
   Property AUDIO_CHANNEL_TYPE = Property.internalClosedChoise("xmpDM:audioChannelType", "Mono", "Stereo", "5.1", "7.1");
   Property AUDIO_COMPRESSOR = Property.internalText("xmpDM:audioCompressor");
   Property COMPILATION = Property.externalInteger("xmpDM:compilation");
   Property COMPOSER = Property.externalText("xmpDM:composer");
   Property COPYRIGHT = Property.externalText("xmpDM:copyright");
   Property DISC_NUMBER = Property.externalInteger("xmpDM:discNumber");
   Property DURATION = Property.externalReal("xmpDM:duration");
   Property ENGINEER = Property.externalText("xmpDM:engineer");
   Property FILE_DATA_RATE = Property.internalRational("xmpDM:fileDataRate");
   Property GENRE = Property.externalText("xmpDM:genre");
   Property INSTRUMENT = Property.externalText("xmpDM:instrument");
   Property KEY = Property.internalClosedChoise("xmpDM:key", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B");
   Property LOG_COMMENT = Property.externalText("xmpDM:logComment");
   Property LOOP = Property.internalBoolean("xmpDM:loop");
   Property NUMBER_OF_BEATS = Property.internalReal("xmpDM:numberOfBeats");
   Property METADATA_MOD_DATE = Property.internalDate("xmpDM:metadataModDate");
   Property PULL_DOWN = Property.internalClosedChoise("xmpDM:pullDown", "WSSWW", "SSWWW", "SWWWS", "WWWSS", "WWSSW", "WSSWW_24p", "SSWWW_24p", "SWWWS_24p", "WWWSS_24p", "WWSSW_24p");
   Property RELATIVE_PEAK_AUDIO_FILE_PATH = Property.internalURI("xmpDM:relativePeakAudioFilePath");
   Property RELEASE_DATE = Property.externalDate("xmpDM:releaseDate");
   Property SCALE_TYPE = Property.internalClosedChoise("xmpDM:scaleType", "Major", "Minor", "Both", "Neither");
   Property SCENE = Property.externalText("xmpDM:scene");
   Property SHOT_DATE = Property.externalDate("xmpDM:shotDate");
   Property SHOT_LOCATION = Property.externalText("xmpDM:shotLocation");
   Property SHOT_NAME = Property.externalText("xmpDM:shotName");
   Property SPEAKER_PLACEMENT = Property.externalText("xmpDM:speakerPlacement");
   Property STRETCH_MODE = Property.internalClosedChoise("xmpDM:stretchMode", "Fixed length", "Time-Scale", "Resample", "Beat Splice", "Hybrid");
   Property TAPE_NAME = Property.externalText("xmpDM:tapeName");
   Property TEMPO = Property.internalReal("xmpDM:tempo");
   Property TIME_SIGNATURE = Property.internalClosedChoise("xmpDM:timeSignature", "2/4", "3/4", "4/4", "5/4", "7/4", "6/8", "9/8", "12/8", "other");
   Property TRACK_NUMBER = Property.externalInteger("xmpDM:trackNumber");
   Property VIDEO_ALPHA_MODE = Property.externalClosedChoise("xmpDM:videoAlphaMode", "straight", "pre-multiplied");
   Property VIDEO_ALPHA_UNITY_IS_TRANSPARENT = Property.internalBoolean("xmpDM:videoAlphaUnityIsTransparent");
   Property VIDEO_COLOR_SPACE = Property.internalClosedChoise("xmpDM:videoColorSpace", "sRGB", "CCIR-601", "CCIR-709");
   Property VIDEO_COMPRESSOR = Property.internalText("xmpDM:videoCompressor");
   Property VIDEO_FIELD_ORDER = Property.internalClosedChoise("xmpDM:videoFieldOrder", "Upper", "Lower", "Progressive");
   Property VIDEO_FRAME_RATE = Property.internalOpenChoise("xmpDM:videoFrameRate", "24", "NTSC", "PAL");
   Property VIDEO_MOD_DATE = Property.internalDate("xmpDM:videoModDate");
   Property VIDEO_PIXEL_DEPTH = Property.internalClosedChoise("xmpDM:videoPixelDepth", "8Int", "16Int", "32Int", "32Float");
   Property VIDEO_PIXEL_ASPECT_RATIO = Property.internalRational("xmpDM:videoPixelAspectRatio");

   /** @deprecated */
   @Deprecated
   public static class ChannelTypePropertyConverter {
      private static Property property;

      public static String convert(Object value) {
         if (value instanceof String) {
            return (String)value;
         } else {
            if (value instanceof Integer) {
               int channelCount = (Integer)value;
               if (channelCount == 1) {
                  return "Mono";
               }

               if (channelCount == 2) {
                  return "Stereo";
               }

               if (channelCount == 5) {
                  return "5.1";
               }

               if (channelCount == 7) {
                  return "7.1";
               }
            }

            return null;
         }
      }

      public static void convertAndSet(Metadata metadata, Object value) {
         if (value instanceof Integer || value instanceof Long) {
            metadata.set(property, convert(value));
         }

         if (value instanceof Date) {
            metadata.set(property, (Date)value);
         }

         if (value instanceof String) {
            metadata.set(property, (String)value);
         }

      }

      static {
         property = XMPDM.AUDIO_CHANNEL_TYPE;
      }
   }
}
