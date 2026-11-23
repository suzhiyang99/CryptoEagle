package org.apache.wicket.util.io;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

public final class FullyBufferedReader {
   private final String input;
   private int inputPosition;
   private int lineNumber;
   private int columnNumber;
   private int lastLineCountIndex;
   private int positionMarker;

   public FullyBufferedReader(Reader reader) throws IOException {
      this(Streams.readString(reader));
   }

   public FullyBufferedReader(String input) {
      this.lineNumber = 1;
      this.columnNumber = 1;
      this.input = input;
   }

   public final CharSequence getSubstring(int toPos) {
      if (toPos < 0) {
         toPos = this.input.length();
      } else if (toPos < this.positionMarker) {
         return "";
      }

      return this.input.subSequence(this.positionMarker, toPos);
   }

   public final CharSequence getSubstring(int fromPos, int toPos) {
      return this.input.subSequence(fromPos, toPos);
   }

   public final int getPosition() {
      return this.inputPosition;
   }

   public final void setPositionMarker(int pos) {
      this.positionMarker = pos;
   }

   public String toString() {
      return this.input;
   }

   public final void countLinesTo(int end) {
      for(int i = this.lastLineCountIndex; i < end; ++i) {
         char ch = this.input.charAt(i);
         if (ch == '\n') {
            this.columnNumber = 1;
            ++this.lineNumber;
         } else if (ch != '\r') {
            ++this.columnNumber;
         }
      }

      this.lastLineCountIndex = end;
   }

   public final int find(char ch) {
      return this.input.indexOf(ch, this.inputPosition);
   }

   public final int find(char ch, int startPos) {
      return this.input.indexOf(ch, startPos);
   }

   public final int find(String str) {
      return this.input.indexOf(str, this.inputPosition);
   }

   public final int find(String str, int startPos) {
      return this.input.indexOf(str, startPos);
   }

   public int findOutOfQuotes(char ch, int startPos) throws ParseException {
      return this.findOutOfQuotes(ch, startPos, '\u0000');
   }

   public int findOutOfQuotes(char ch, int startPos, char quotationChar) throws ParseException {
      int closeBracketIndex = this.find(ch, startPos + 1);
      if (closeBracketIndex != -1) {
         CharSequence tagCode = this.getSubstring(startPos, closeBracketIndex + 1);

         for(int i = 0; i < tagCode.length(); ++i) {
            char currentChar = tagCode.charAt(i);
            char previousTag = tagCode.charAt(i > 0 ? i - 1 : 0);
            if (quotationChar != 0 || currentChar != '\'' && currentChar != '"') {
               if (currentChar == quotationChar && previousTag != '\\') {
                  quotationChar = 0;
               }
            } else {
               quotationChar = currentChar;
               this.countLinesTo(startPos + i);
            }

            if (currentChar == ch && quotationChar != 0) {
               return this.findOutOfQuotes(ch, closeBracketIndex + 1, quotationChar);
            }
         }
      } else if (quotationChar != 0) {
         throw new ParseException("Opening/closing quote not found for quote at (line " + this.getLineNumber() + ", column " + this.getColumnNumber() + ")", startPos);
      }

      return closeBracketIndex;
   }

   public final void setPosition(int pos) {
      this.inputPosition = pos;
   }

   public final int getColumnNumber() {
      return this.columnNumber;
   }

   public final int getLineNumber() {
      return this.lineNumber;
   }

   public final int size() {
      return this.input.length();
   }

   public final char charAt(int pos) {
      return this.input.charAt(pos);
   }
}
