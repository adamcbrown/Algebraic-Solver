package io.github.adamcbrown1997.algebraicSolver.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.github.adamcbrown1997.algebraicSolver.expression.Expression;
import io.github.adamcbrown1997.algebraicSolver.expression.Function;

public class CLI {
	public static void main(String[] args){
		Expression.initialize();
		CLI cli=new CLI();
		try {
			cli.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private final BufferedReader input;
	
	public CLI(){
		input = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public void run() throws IOException{
		boolean shouldExit=false;
		while(!shouldExit){
			String info=input.readLine();
			String command=info.split(" ", 2)[0];
			try{
				if(command.equalsIgnoreCase("solve")){
					try {
						String out="{";
						int answers=0;
						double lastAnswer=0;
						for(double d : new Expression(info.split(" ", 2)[1]).evaluate()){
							out+=d+", ";
							answers++;
							lastAnswer=d;
						}
						if(answers==0){
							System.out.println("{}");
						}else if(answers==1){
							System.out.println(lastAnswer);
						}else{
							System.out.println(out.substring(0, out.length()-2)+"}");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if(command.equalsIgnoreCase("define_method")){
					System.out.println("Enter the head of the function (i.e. 'sin(x)'). Parameters are separated by commas");
					info=input.readLine();
					String functionName=info.substring(0, info.indexOf('('));
					String[] variables=info.substring(info.indexOf('(')+1, info.indexOf(')')).split(",");
					if(Expression.isAConstant(functionName)){
						throw new Exception("'"+functionName+"' is defined as a constant");
					}
					System.out.println("Enter the expression of this function");
					info=input.readLine();
					Function defined=new Function(functionName, info, variables);
					if(defined.isValidAsFunction()){
						Expression.addFunction(defined);
					}else{
						throw new Exception("Not a valid expression");
					}
				}else{
					System.out.println("DIDN'T WORK");
				}
			}catch(Exception e){
				System.out.println(e.getLocalizedMessage());
			}
		}
	}
}
