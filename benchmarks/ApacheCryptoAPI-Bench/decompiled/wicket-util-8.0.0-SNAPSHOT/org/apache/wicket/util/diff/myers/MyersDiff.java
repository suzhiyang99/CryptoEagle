package org.apache.wicket.util.diff.myers;

import org.apache.wicket.util.diff.Chunk;
import org.apache.wicket.util.diff.Delta;
import org.apache.wicket.util.diff.DiffAlgorithm;
import org.apache.wicket.util.diff.DifferentiationFailedException;
import org.apache.wicket.util.diff.Revision;

public class MyersDiff implements DiffAlgorithm {
   public Revision diff(Object[] orig, Object[] rev) throws DifferentiationFailedException {
      PathNode path = buildPath(orig, rev);
      return buildRevision(path, orig, rev);
   }

   public static PathNode buildPath(Object[] orig, Object[] rev) throws DifferentiationFailedException {
      if (orig == null) {
         throw new IllegalArgumentException("original sequence is null");
      } else if (rev == null) {
         throw new IllegalArgumentException("revised sequence is null");
      } else {
         int N = orig.length;
         int M = rev.length;
         int MAX = N + M + 1;
         int size = 1 + 2 * MAX;
         int middle = (size + 1) / 2;
         PathNode[] diagonal = new PathNode[size];
         diagonal[middle + 1] = new Snake(0, -1, (PathNode)null);

         for(int d = 0; d < MAX; ++d) {
            for(int k = -d; k <= d; k += 2) {
               int kmiddle = middle + k;
               int kplus = kmiddle + 1;
               int kminus = kmiddle - 1;
               PathNode prev = null;
               int i;
               if (k != -d && (k == d || diagonal[kminus].i >= diagonal[kplus].i)) {
                  i = diagonal[kminus].i + 1;
                  prev = diagonal[kminus];
               } else {
                  i = diagonal[kplus].i;
                  prev = diagonal[kplus];
               }

               diagonal[kminus] = null;
               int j = i - k;

               PathNode node;
               for(node = new DiffNode(i, j, prev); i < N && j < M && orig[i].equals(rev[j]); ++j) {
                  ++i;
               }

               if (i > node.i) {
                  node = new Snake(i, j, node);
               }

               diagonal[kmiddle] = node;
               if (i >= N && j >= M) {
                  return diagonal[kmiddle];
               }
            }

            diagonal[middle + d - 1] = null;
         }

         throw new DifferentiationFailedException("could not find a diff path");
      }
   }

   public static Revision buildRevision(PathNode path, Object[] orig, Object[] rev) {
      if (path == null) {
         throw new IllegalArgumentException("path is null");
      } else if (orig == null) {
         throw new IllegalArgumentException("original sequence is null");
      } else if (rev == null) {
         throw new IllegalArgumentException("revised sequence is null");
      } else {
         Revision revision = new Revision();
         if (path.isSnake()) {
            path = path.prev;
         }

         while(path != null && path.prev != null && path.prev.j >= 0) {
            if (path.isSnake()) {
               throw new IllegalStateException("bad diffpath: found snake when looking for diff");
            }

            int i = path.i;
            int j = path.j;
            path = path.prev;
            int ianchor = path.i;
            int janchor = path.j;
            Delta delta = Delta.newDelta(new Chunk(orig, ianchor, i - ianchor), new Chunk(rev, janchor, j - janchor));
            revision.insertDelta(delta);
            if (path.isSnake()) {
               path = path.prev;
            }
         }

         return revision;
      }
   }
}
