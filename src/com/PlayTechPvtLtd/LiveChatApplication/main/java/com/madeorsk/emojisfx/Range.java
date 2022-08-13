package com.PlayTechPvtLtd.LiveChatApplication.main.java.com.madeorsk.emojisfx;

public class Range
{
  private int start = 0;
  private int end = 0;
  private Runnable action;

  public Range(int start, int end)
  {
    if (start < end)
    {
      this.start = start;
      this.end = end;
    }
    else
    {
      this.start = end;
      this.end = start;
    }
  }

  public Range(int start, int end, Runnable action)
  {
    this(start, end);
    this.setAssociatedAction(action);
  }

  public int getStart()
  {
    return this.start;
  }

  public int getEnd()
  {
    return this.end;
  }

  public void setAssociatedAction(Runnable action)
  {
    this.action = action;
  }

  public Runnable getAssociatedAction()
  {
    return this.action;
  }

  public boolean isInRange(int number)
  {
    return this.start <= number && number <= this.end;
  }

  public boolean isOutOfRange(int number)
  {
    return number < this.start || this.end < number;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Range)
      return ((Range) obj).getStart() == this.getStart() && ((Range) obj).getEnd() == this.getEnd();
    else
      return false;
  }
}
