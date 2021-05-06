package com.mygdx.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class jogo extends ApplicationAdapter {

	//Variáveis das imagens

	//É o que renderiza as coisas na tela
	private SpriteBatch batch;
	//Array das imagens de pássaro
	private Texture [] passaros;
	//Background
	private Texture fundo;

	//Eixos de movimentação
	private int movimentaY = 0;
	private int movimentaX = 0;
	//O que auxilia na animação
	private float variacao = 0;
	private float gravidade = 0;
	//Onde o pássaro nasce
	private  float posicaoInicialVerticalPassaro = 0;

	//Tela do Dispositivo
	private float larguraDispositivo;  //para colocar a largura do celular
	private float alturaDispositivo;   //para colocar a altura do celular


//O que faz a tela aparecer
	@Override
	public void create () {
		//Instancia o objeto
		batch = new SpriteBatch();
		//Pássaros que vai ser usado
		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");
		//Textura do bg
		fundo = new Texture("fundo.png");

		//Chamando a largura do dispositivo
		larguraDispositivo = Gdx.graphics.getWidth();
		//Chamando a altura do dispositivo
		alturaDispositivo = Gdx.graphics.getHeight();
		//Coloca a posição inicial como metade da altura do dispotivo = Meio da tela
		posicaoInicialVerticalPassaro = alturaDispositivo / 2;
	}

	//Isso é quase um update
	@Override
	public void render () {
		//Excutar
		batch.begin();

		//Troca das animações
		if(variacao > 3)
			variacao = 0;

		//Verifica o touch
		boolean toqueTela = Gdx.input.justTouched();

		//Impulso para cima
		if(Gdx.input.justTouched()){
			gravidade = -25;
		}

		//Fazendo com que o pássaro pulo com o toque
		if(posicaoInicialVerticalPassaro > 0 || toqueTela)
			posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;

		//Desenhando o fundo de acordo com o tamanho do dispositivo
		batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
		//Coloca o pássaro na posição de acordo com a posição vertical (que está sendo modificado pelo dispositivo)
		batch.draw(passaros[(int) variacao], movimentaX, posicaoInicialVerticalPassaro);

		//Gráficos do Gdx e utiliza na variação das animações
		variacao += Gdx.graphics.getDeltaTime() * 10;

		gravidade++;
		movimentaY++;
		movimentaX++;

		//Parou a ação
		batch.end();
	}

	//Entrega a aplicação e depois retorna os dados
	@Override
	public void dispose () {
	}
}
