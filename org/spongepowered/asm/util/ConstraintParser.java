package org.spongepowered.asm.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.util.throwables.ConstraintViolationException;
import org.spongepowered.asm.util.throwables.InvalidConstraintException;

public final class ConstraintParser {
  public static class Constraint {
    public static final Constraint NONE = new Constraint();
    
    private static final Pattern pattern = Pattern.compile("^([A-Z0-9\\-_\\.]+)\\((?:(<|<=|>|>=|=)?([0-9]+)(<|(-)([0-9]+)?|>|(\\+)([0-9]+)?)?)?\\)$");
    
    private final String expr;
    
    private String token;
    
    private String[] constraint;
    
    private int min = Integer.MIN_VALUE;
    
    private int max = Integer.MAX_VALUE;
    
    private Constraint next;
    
    Constraint(String expr) {
      this.expr = expr;
      Matcher matcher = pattern.matcher(expr);
      if (!matcher.matches())
        throw new InvalidConstraintException("Constraint syntax was invalid parsing: " + this.expr); 
      this.token = matcher.group(1);
      this
        
        .constraint = new String[] { matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5), matcher.group(6), matcher.group(7), matcher.group(8) };
      parse();
    }
    
    private Constraint() {
      this.expr = null;
      this.token = "*";
      this.constraint = new String[0];
    }
    
    private void parse() {
      if (!has(1))
        return; 
      this.max = this.min = val(1);
      boolean hasModifier = has(0);
      if (has(4)) {
        if (hasModifier)
          throw new InvalidConstraintException("Unexpected modifier '" + elem(0) + "' in " + this.expr + " parsing range"); 
        this.max = val(4);
        if (this.max < this.min)
          throw new InvalidConstraintException("Invalid range specified '" + this.max + "' is less than " + this.min + " in " + this.expr); 
        return;
      } 
      if (has(6)) {
        if (hasModifier)
          throw new InvalidConstraintException("Unexpected modifier '" + elem(0) + "' in " + this.expr + " parsing range"); 
        this.max = this.min + val(6);
        return;
      } 
      if (hasModifier) {
        if (has(3))
          throw new InvalidConstraintException("Unexpected trailing modifier '" + elem(3) + "' in " + this.expr); 
        String leading = elem(0);
        if (">".equals(leading)) {
          this.min++;
          this.max = Integer.MAX_VALUE;
        } else if (">=".equals(leading)) {
          this.max = Integer.MAX_VALUE;
        } else if ("<".equals(leading)) {
          this.max = --this.min;
          this.min = Integer.MIN_VALUE;
        } else if ("<=".equals(leading)) {
          this.max = this.min;
          this.min = Integer.MIN_VALUE;
        } 
      } else if (has(2)) {
        String trailing = elem(2);
        if ("<".equals(trailing)) {
          this.max = this.min;
          this.min = Integer.MIN_VALUE;
        } else {
          this.max = Integer.MAX_VALUE;
        } 
      } 
    }
    
    private boolean has(int index) {
      return (this.constraint[index] != null);
    }
    
    private String elem(int index) {
      return this.constraint[index];
    }
    
    private int val(int index) {
      return (this.constraint[index] != null) ? Integer.parseInt(this.constraint[index]) : 0;
    }
    
    void append(Constraint next) {
      if (this.next != null) {
        this.next.append(next);
        return;
      } 
      this.next = next;
    }
    
    public String getToken() {
      return this.token;
    }
    
    public int getMin() {
      return this.min;
    }
    
    public int getMax() {
      return this.max;
    }
    
    public void check(ITokenProvider environment) throws ConstraintViolationException {
      if (this != NONE) {
        Integer value = environment.getToken(this.token);
        if (value == null)
          throw new ConstraintViolationException("The token '" + this.token + "' could not be resolved in " + environment, this); 
        if (value.intValue() < this.min)
          throw new ConstraintViolationException("Token '" + this.token + "' has a value (" + value + ") which is less than the minimum value " + this.min + " in " + environment, this, value
              .intValue()); 
        if (value.intValue() > this.max)
          throw new ConstraintViolationException("Token '" + this.token + "' has a value (" + value + ") which is greater than the maximum value " + this.max + " in " + environment, this, value
              .intValue()); 
      } 
      if (this.next != null)
        this.next.check(environment); 
    }
    
    public String getRangeHumanReadable() {
      if (this.min == Integer.MIN_VALUE && this.max == Integer.MAX_VALUE)
        return "ANY VALUE"; 
      if (this.min == Integer.MIN_VALUE)
        return String.format("less than or equal to %d", new Object[] { Integer.valueOf(this.max) }); 
      if (this.max == Integer.MAX_VALUE)
        return String.format("greater than or equal to %d", new Object[] { Integer.valueOf(this.min) }); 
      if (this.min == this.max)
        return String.format("%d", new Object[] { Integer.valueOf(this.min) }); 
      return String.format("between %d and %d", new Object[] { Integer.valueOf(this.min), Integer.valueOf(this.max) });
    }
    
    public String toString() {
      return String.format("Constraint(%s [%d-%d])", new Object[] { this.token, Integer.valueOf(this.min), Integer.valueOf(this.max) });
    }
  }
  
  public static Constraint parse(String expr) {
    if (expr == null || expr.length() == 0)
      return Constraint.NONE; 
    String[] exprs = expr.replaceAll("\\s", "").toUpperCase().split(";");
    Constraint head = null;
    for (String subExpr : exprs) {
      Constraint next = new Constraint(subExpr);
      if (head == null) {
        head = next;
      } else {
        head.append(next);
      } 
    } 
    return (head != null) ? head : Constraint.NONE;
  }
  
  public static Constraint parse(AnnotationNode annotation) {
    String constraints = Annotations.<String>getValue(annotation, "constraints", "");
    return parse(constraints);
  }
}