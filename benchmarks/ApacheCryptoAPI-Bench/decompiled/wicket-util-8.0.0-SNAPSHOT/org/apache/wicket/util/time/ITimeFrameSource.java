package org.apache.wicket.util.time;

import java.io.Serializable;

public interface ITimeFrameSource extends Serializable {
   TimeFrame getTimeFrame();
}
