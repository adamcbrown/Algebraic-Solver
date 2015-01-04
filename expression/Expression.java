package io.github.adamcbrown1997.algebraicSolver.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Expression {
	
	private enum ExpressionType{
		STRING, EXPRESSIONS, NUMBER, BUILT_IN_FUNCTION
	}
	
	private ExpressionType type;
	
	protected String expression;
	
	private Expression e1;
	private char symbol;
	private Expression e2;
	
	private String function;
	
	private double numbers[];

	private static List<Function> definedFunctions=new ArrayList<Function>();
	private static Map<String, Double> definedConstants=new HashMap<String, Double>();
	
	public static void initialize(){
		definedConstants.put("e", Math.E);
		definedConstants.put("pi", Math.PI);
	}
	
	public static void addFunction(Function function){
		if(definedFunctions.contains(function)){
			definedFunctions.remove(function);
		}
		definedFunctions.add(function);
	}
	
	public static boolean isADefinedFunction(String head, int params){
		for(Function f:definedFunctions){
			if(f.getHead().equalsIgnoreCase(head)&&f.getNumOfParams()==params){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isAFunction(String head, int params){
		return  isADefinedFunction(head, params)||
				head.startsWith("log")||
				head.equalsIgnoreCase("ln")||
				head.startsWith("sin")||
				head.startsWith("cos")||
				head.startsWith("tan")||
				head.startsWith("asin")||
				head.startsWith("acos")||
				head.startsWith("atan")||
				head.startsWith("sqrt");
	}
	
	public static void addConstant(String name, double value){
		definedConstants.put(name, value);
	}
	
	public static boolean isAConstant(String name){
		return definedConstants.containsKey(name);
	}
	
	public Expression(String expression){
		type=ExpressionType.STRING;
		this.expression=expression;
	}
	
	public Expression(Expression e1, char symbol, Expression e2){
		type=ExpressionType.EXPRESSIONS;
		this.e1=e1;
		this.symbol=symbol;
		this.e2=e2;
	}
	
	public Expression(String function, Expression e1){
		type=ExpressionType.BUILT_IN_FUNCTION;
		this.function=function;
		this.e1=e1;
	}
	
	public Expression(double[] numbers){
		type=ExpressionType.NUMBER;
		this.numbers=numbers;
	}
	
	/**
	 * @param expression
	 * @param location
	 * @return the name of the constant in the first index and its value in the second, or null if not a constant
	 */
	private Object[] getConstantData(String expression, int location){
		for(String constant:definedConstants.keySet()){
			if(location+constant.length()<=expression.length()){
				if(expression.substring(location, location+constant.length()).equalsIgnoreCase(constant) &&
						(location==0||!EquationUtilities.isLetter(expression.charAt(location-1))) &&
						(location==expression.length()-constant.length()||!EquationUtilities.isLetter(expression.charAt(location+constant.length())))){
					return new Object[]{constant, definedConstants.get(constant)};
				}
			}
		}
		return null;
	}
	
	public double[] solve() throws Exception{
		switch(type){
		case STRING:
			for(int i=0;i<expression.length();i++){
				Object[] constantData=getConstantData(expression, i);
				
				if(constantData!=null){
					if(i==expression.length()-1){
						expression=expression.substring(0,i)+constantData[1];
					}else{
						expression=expression.substring(0,i)+constantData[1]+expression.substring(i+((String)constantData[0]).length());
					}
				}
//				if(expression.charAt(i)=='e'&& (i==0||!EquationUtilities.isLetter(expression.charAt(i-1))) && (i==expression.length()-1||!EquationUtilities.isLetter(expression.charAt(i+1)))){
//					if(i==expression.length()-1){
//						expression=expression.substring(0,i)+Math.E;
//					}else{
//						expression=expression.substring(0,i)+Math.E+expression.substring(i+1);
//					}
//				}
//
//				if((expression.charAt(i)=='p'&&expression.charAt(i+1)=='i') && (i==0||!EquationUtilities.isLetter(expression.charAt(i-1))) && (i==expression.length()-2||!EquationUtilities.isLetter(expression.charAt(i+2)))){
//					if(i==expression.length()-2){
//						expression=expression.substring(0,i)+Math.PI;
//					}else{
//						expression=expression.substring(0,i)+Math.PI+expression.substring(i+2);
//					}
//				}
			}
			
			for(int i=0;i<expression.length();i++){
				if(expression.charAt(i)=='-'){
					int depth=0;
					int end=-1;
					for(int subI=i+1;subI<expression.length();subI++){
						char c=expression.charAt(subI);
						if(c=='('){
							depth++;
						}
						if(c==')'){
							depth--;
							if(depth==0){
								end=subI;
								break;
							}
						}
						if(((c<'0'|| c>'9')&&c!='.')&& depth==0){
							end=subI;
							break;
						}
					}
					
					if(end==-1){
						end=expression.length();
					}
					
					if(i==0){
						return new Expression("(0-"+expression.substring(1, end)+")"+ (end==expression.length()?"":expression.substring(end))).solve();
					}
					
					char before=expression.charAt(i-1);
					if(before=='('||EquationUtilities.isOperation(before)){
						return new Expression(expression.substring(0, i)+"(0-"+expression.substring(i+1, end)+")"+ (end==expression.length()?"":expression.substring(end))).solve();
					}
				}
			}
			if(expression.contains("~")){
				double[] ans1=new Expression(expression.replaceFirst("~", "+")).solve();
				double[] ans2=new Expression(expression.replaceFirst("~", "-")).solve();
				
				ArrayList<Double> answers=new ArrayList<Double>();
				for(int i=0;i<ans1.length;i++){
					if(!answers.contains(ans1[i])){
						answers.add(ans1[i]);
					}
				}
				
				for(int i=0;i<ans2.length;i++){
					if(!answers.contains(ans2[i])){
						answers.add(ans2[i]);
					}
				}
				
				double[] ret=new double[answers.size()];
				for(int i=0;i<answers.size();i++){
					ret[i]=answers.get(i);
				}
				return ret;
			}
			
			if(EquationUtilities.countedCharacters(expression, '(')!=EquationUtilities.countedCharacters(expression, ')')){
				throw new Exception("Number of open parentheses does not equal the number of close parentheses");
			}
			
			try{
				return new double[]{Double.parseDouble(expression)};//First try to simply parse the number
			}catch(NumberFormatException e){
				if(expression.contains("(")){
					int depth=0;
					int start=0;
					for(int i=0;i<expression.length();i++){
						if(depth==0&&EquationUtilities.isLetter(expression.charAt(i))){
							int split=i-1;
							if(split==-1){
								int closeParen=-1;
								int subDepth=1;
								int openParen=expression.indexOf('(');
								
								for(int subI=openParen+1;subI<expression.length();subI++){
									if(expression.charAt(subI)=='('){
										subDepth++;
									}
									
									if(expression.charAt(subI)==')'){
										subDepth--;
										if(subDepth==0){
											closeParen=subI;
											break;
										}
									}
								}
								
								double[] funcValue=null;
								String[] parametersString=expression.substring(openParen+1,closeParen).split(",");
								Expression[] parameters=new Expression[parametersString.length];
								for(int i2=0;i2<parameters.length;i2++){
									parameters[i2]=new Expression(parametersString[i2]);
								}
								
								if(isADefinedFunction(expression.substring(0, openParen), parameters.length)){
									for(Function f:definedFunctions){
										if(f.getHead().equalsIgnoreCase(expression.substring(0, openParen))&&f.getNumOfParams()==parameters.length){
											funcValue=f.solve(parameters);
										}
									}
								}else{
									funcValue = new Expression(expression.substring(0, openParen), new Expression(expression.substring(openParen,closeParen+1))).solve();
								}
								if(closeParen==expression.length()-1){
									return funcValue;
								}else{
									return new Expression(new Expression(funcValue), expression.charAt(closeParen+1), new Expression(expression.substring(closeParen+2))).solve();
								}
							}else{
								return new Expression(new Expression(expression.substring(0, split)), expression.charAt(split), new Expression(expression.substring(split+1))).solve();
							}
						}
						
						if(expression.charAt(i)=='('){
							if(depth==0){
								start=i;
							}
							depth++;
						}
						
						if(expression.charAt(i)==')'){
							depth--;
							if(depth==0){
								
								if(start==0&&i==expression.length()-1){//Occurs in situations like (5+4), which should reduce to 5+4
									return new Expression(expression.substring(1, expression.length()-1)).solve();
								}
								
								if(i!=expression.length()-1&&EquationUtilities.isLowestOp(expression.charAt(i+1), expression)){
									return new Expression(new Expression(expression.substring(0, start-1)), expression.charAt(start-1), new Expression(expression.substring(start))).solve();
								}
							}
						}
					}
				}
				
				for(char symbol : EquationUtilities.OPERATIONS){
					int depth=0;
					for(int i=expression.length()-1;i>=0;i--){
						if(expression.charAt(i)==')'){
							depth++;
						}else if(expression.charAt(i)=='('){
							depth--;
						}
						
						if(expression.charAt(i)==symbol&&depth==0){
							Expression e1=new Expression(expression.substring(0, i));
							Expression e2=new Expression(expression.substring(i+1));
							return new Expression(e1, symbol, e2).solve();
						}
					}
				}
			}
			break;
		case EXPRESSIONS:
			double[] e1s=e1.solve(), e2s=e2.solve();
			double[] ret=new double[e1s.length*e2s.length*(symbol=='~'?2:1)];
			for(int a=0;a<e1s.length;a++){
				for(int b=0;b<e2s.length;b++){
					switch(symbol){
					case '+':
						ret[a*e2s.length+b]=e1.solve()[a]+e2.solve()[b];
						break;
					case '-':
						ret[a*e2s.length+b]=e1.solve()[a]-e2.solve()[b];
						break;
					case '*':
						ret[a*e2s.length+b]=e1.solve()[a]*e2.solve()[b];
						break;
					case '/':
						ret[a*e2s.length+b]=e1.solve()[a]/e2.solve()[b];
						break;
					case '^':
						ret[a*e2s.length+b]=Math.pow(e1.solve()[a],e2.solve()[b]);
						break;
					}
				}
			}
			return ret;
		case BUILT_IN_FUNCTION:
			double[] solved=e1.solve();
			int length=solved.length;

			if(function.startsWith("log")){
				if(!function.equalsIgnoreCase("log")){
					length*=new Expression(function.substring(3)).solve().length;
				}
			}
			
			if(function.equalsIgnoreCase("asin")||function.equalsIgnoreCase("acos")||function.equalsIgnoreCase("atan")){
				length*=2;
			}
			
			ret=new double[length];
			
			for(int i=0;i<solved.length;i++){
				if(function.startsWith("log")){
					if(function.equalsIgnoreCase("log")){
						ret[i]=Math.log10(solved[i]);
					}else{
						Expression base=new Expression(function.substring(3));
						double[] baseSolved=base.solve();
						for(int i2=0;i2<baseSolved.length;i2++){
							ret[i+i2*solved.length]= Math.log(solved[i])/Math.log(baseSolved[i2]);
						}
					}
				}else if(function.equalsIgnoreCase("ln")){
					ret[i]= Math.log(solved[i]);
				}else if(function.equalsIgnoreCase("sin")){
					ret[i]= Math.sin(solved[i]);
				}else if(function.equalsIgnoreCase("cos")){
					ret[i]= Math.cos(solved[i]);
				}else if(function.equalsIgnoreCase("tan")){
					ret[i]= Math.tan(solved[i]);
				}else if(function.equalsIgnoreCase("asin")){
					ret[i]= Math.asin(solved[i]);
					ret[i+solved.length]=Math.PI-Math.asin(solved[i]);
				}else if(function.equalsIgnoreCase("acos")){
					ret[i]= Math.acos(solved[i]);
					ret[i+solved.length]=-Math.acos(solved[i]);
				}else if(function.equalsIgnoreCase("atan")){
					ret[i]= Math.atan(solved[i]);
					ret[i+solved.length]=Math.atan(solved[i])+Math.PI;
				}else if(function.equalsIgnoreCase("sqrt")){
					ret[i]=Math.sqrt(solved[i]);
				}
			}
			return ret;
		case NUMBER:
			return numbers;
		default:
			throw new Exception("Unsupported use of a symbol or function");
		}
		System.out.println(expression);
		throw new Exception("UH OH");
	}

	public double[] evaluate() throws Exception {
		double[] answers=solve();
		ArrayList<Double> compressed=new ArrayList<Double>();
		for(double answer : answers){
			if(answer!=Double.NaN){
				if(!compressed.contains(answers)){
					compressed.add(answer);
				}
			}
		}
		
		double[] ret=new double[compressed.size()];
		for(int i=0;i<compressed.size();i++){
			ret[i]=compressed.get(i);
		}
		
		return ret;
	}
}
