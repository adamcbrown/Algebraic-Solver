package io.github.adamcbrown1997.algebraicSolver.examples;

import io.github.adamcbrown1997.algebraicSolver.expression.Expression;

public class Test {
	public static void main(String[] args){
		try {
			double[] answers=new Expression("sin(45*pi/180)").solve();
			for(int i=0;i<answers.length;i++){
				System.out.println(answers[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
