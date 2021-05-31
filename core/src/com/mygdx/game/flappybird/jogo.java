package com.mygdx.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class jogo extends ApplicationAdapter {

	//Variáveis das imagens

	//É o que renderiza as coisas na tela
	private SpriteBatch batch;
	//Array das imagens de pássaro
	private Texture [] passaros;
	//Background
	private Texture fundo;

	//Texturas do cano e fim de jogo
    private Texture canoSuperior;
    private Texture canoInferior;
    private Texture gameOver;

    //Pontuação e estado de jogo
    private int pontuacaoMaxima = 0;
    private int pontos = 0;
    private int estadoJogo = 0;

	//Eixos de movimentação
	private int movimentaY = 0;
	private int movimentaX = 0;
	//O que auxilia na animação
	private float variacao = 0;
	private float gravidade = 0;
	//Onde o pássaro nasce
	private  float posicaoInicialVerticalPassaro = 0;
    private float posicaoHorizontalPassaro = 0;

	//Tela do Dispositivo
	private float larguraDispositivo;
	private float alturaDispositivo;

    //Posicionamento do cano
    private float posicaoCanoHorizontal = 0;
    private float posicaoCanoVertical = 0;
    //Calcula espaço entre os canos
    private float espaçoEntreCanos;

    //Randomizador dos canos
    private Random random;

    //Verificador se passou no cano
    private boolean passouCano = false;

    //Bitmap dos textos que serão renderizados
    BitmapFont textoPontuacao;
    BitmapFont textoReiniciar;
    BitmapFont textoMelhorPontuacao;

    //variaveis dos sons do jogo
    Sound somColisao;
    Sound somVoar;
    Sound somPontos;

    //Renderização e criação dos colisores
    private ShapeRenderer shapeRenderer;
    private Circle circulopassaro;
    private Rectangle retanguloCanoSuperior;
    private Rectangle retanguloCanoInferior;

    Preferences preferencias;

//O que faz a tela aparecer
	@Override
	public void create () {
        inicializaImagens();
        inicializaTela();
    }

    private void inicializaTela() {
        //Instancia o objeto
        batch = new SpriteBatch();
        //Chamando a largura do dispositivo
        larguraDispositivo = Gdx.graphics.getWidth();
        //Chamando a altura do dispositivo
        alturaDispositivo = Gdx.graphics.getHeight();
        //Coloca a posição inicial como metade da altura do dispotivo = Meio da tela
        posicaoInicialVerticalPassaro = alturaDispositivo / 2;

        //Renderização de títulos
        textoPontuacao = new BitmapFont();
        textoPontuacao.setColor( Color.WHITE);
        textoPontuacao.getData().setScale(10);

        textoMelhorPontuacao = new BitmapFont();
        textoMelhorPontuacao.setColor(Color.RED);
        textoMelhorPontuacao.getData().setScale(2);

        textoReiniciar = new BitmapFont();
        textoReiniciar.setColor(Color.GREEN);
        textoReiniciar.getData().setScale(2);

        //Inicialização de colliders
        shapeRenderer = new ShapeRenderer();
        circulopassaro = new Circle();
        retanguloCanoSuperior = new Rectangle();
        retanguloCanoInferior = new Rectangle();

        //Chamada de sons
        somVoar = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
        somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
        somPontos = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

        //Quase um playerprefs pra guardar a pontuação
        preferencias = Gdx.app.getPreferences("flappyBird");
        pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);
    }

    private void inicializaImagens() {
        //Pássaros que vai ser usado
        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");
        //Textura do bg
        fundo = new Texture("fundo.png");
        //Textura dos canos
        canoSuperior = new Texture("cano_topo_maior.png");
        canoInferior = new Texture("cano_baixo_maior.png");
        //Textura Game Over
        gameOver = new Texture("game_over.png");
    }

    //Isso é quase um update
	@Override
	public void render () {
        verificarEstadoDoJogo();
        validarPontos();
        desenhaImagens();
        detectarColisao();
    }

    private void validarPontos() {
	    //Se o pássaro tiver passando no meio do cano
        if (posicaoCanoHorizontal < 50 - passaros[0].getWidth())
        {
            //Se passou o cano e for falso, soma um ponto, coloca a variável em verdadeiro e instancia o som.
            if (!passouCano){
                pontos++;
                passouCano = true;
                somPontos.play();
            }
        }
        //É o famoso deltaTime da Unity na animação do personagem
        variacao += Gdx.graphics.getDeltaTime() * 10;
        if(variacao > 3)
            variacao = 0;
    }

    private void desenhaImagens() {
        //Inicia a renderização
	    batch.begin();
        //Desenha o fundo de acordo com o dispositivo
        batch.draw(fundo,0,0,larguraDispositivo,alturaDispositivo);
        //Desenha o passaro
        batch.draw(passaros[(int) variacao],50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro);
        //Desenha o cano espaçado
        batch.draw( canoInferior, posicaoCanoHorizontal , alturaDispositivo/2 - canoInferior.getHeight() - espaçoEntreCanos/2 + posicaoCanoVertical);
        batch.draw( canoSuperior, posicaoCanoHorizontal ,alturaDispositivo/2 + espaçoEntreCanos/2 + posicaoCanoVertical);//instancia o cano na tela com espaço entre eles
        //Desenha o placar da pontuação
        textoPontuacao.draw( batch, String.valueOf( pontos ),larguraDispositivo /2, alturaDispositivo - 100 );
        //Se o estado do jogo for 2 é fim de jogo
        if(estadoJogo == 2)
        {
            batch.draw(gameOver, larguraDispositivo / 2 +200 - gameOver.getWidth(), alturaDispositivo / 2);
            textoReiniciar.draw(batch, "TOQUE PARA REINICIAR", larguraDispositivo / 2 - 250, alturaDispositivo /2 - gameOver.getHeight() / 2);
            textoMelhorPontuacao.draw(batch, "MELHOR PONTUAÇÃO: " + pontuacaoMaxima +" Pontos", larguraDispositivo /2 - 250, alturaDispositivo /2 - gameOver.getHeight() * 2);
        }
        //End
        batch.end();
    }

    private void verificarEstadoDoJogo() {
	    //Pegar o toque na tela
        boolean toqueTela = Gdx.input.justTouched();
        //Estado do Jogo inicial
        if (estadoJogo == 0) {
            //Se tocar na tela o piriquito cai e começa o jogo
            if (Gdx.input.justTouched())
            {
                gravidade = -20;
                estadoJogo = 1;
                //Solta o som de voo
                somVoar.play();
            }
            //Se o estado for 1 quer dizer que o jogo tá rolando
        } else if (estadoJogo == 1) {
            if (Gdx.input.justTouched()) {
                //Se tocar o piriquito vai pra cima e sucesso, voa
                gravidade = -20;
                somVoar.play();
            }
            //Movimentação dos canos pelo tempo de reprodução do game
            posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 300;
            //Verificação da largura do dipositivo para fazer ele aparecer aleatoriamente num espaço deixando ele falso por não ter passado por ele
            if (posicaoCanoHorizontal < -canoInferior.getWidth()) {
                posicaoCanoHorizontal = larguraDispositivo;
                posicaoCanoHorizontal = random.nextInt( 600 ) - 200;
                passouCano = false;
            }
            //É o que tá verificando a gravidade pra ele cair e depois pular com toque
            if (posicaoInicialVerticalPassaro > 0 || toqueTela)
                posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
            gravidade++;
            //Se o estado for 2 (fim de jogo)
        } else if (estadoJogo == 2) {
            //Se a pontuação for maior que a pontuação máxima troca de pontuação máxima
            if (pontos > pontuacaoMaxima) {
                pontuacaoMaxima = pontos;
                preferencias.putInteger( "pontuacaoMaxima", pontuacaoMaxima );
            }
            //Se o pássaro colide ele volta
            posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500;
            //Se tocar na tela, volta pra tela de início e redefine o estado inicial do jogo
            if (toqueTela) {
                estadoJogo = 0;
                pontos = 0;
                gravidade = 0;
                posicaoHorizontalPassaro = 0;
                posicaoInicialVerticalPassaro = alturaDispositivo / 2;
                posicaoCanoHorizontal = larguraDispositivo;
            }
        }
    }

    private void detectarColisao() {
        //Cria collider do pássaro
        circulopassaro.set(50 + passaros[0].getWidth() / 2, posicaoInicialVerticalPassaro + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);
        //Criação da posição dos coliders do cano de acordo com o dispositivo
        retanguloCanoSuperior.set(posicaoCanoHorizontal, alturaDispositivo / 2 - + espaçoEntreCanos/ 2 + posicaoCanoVertical, canoSuperior.getWidth(), canoSuperior.getHeight() );
        retanguloCanoInferior.set(posicaoCanoHorizontal, alturaDispositivo / 2 - canoInferior.getHeight() - espaçoEntreCanos / 2 + posicaoCanoVertical, canoInferior.getWidth(), canoInferior.getHeight());

        //Detectores de colisão
        boolean colisaoCanoSuperior = Intersector.overlaps(circulopassaro, retanguloCanoSuperior);
        boolean colisaoCanoInferior = Intersector.overlaps(circulopassaro, retanguloCanoInferior);

        //Se o pássaro colider
        if (colisaoCanoInferior || colisaoCanoSuperior)
        {
            //Solta o som de colisão e gameover-
            if(estadoJogo ==1){
                somColisao.play();
                estadoJogo = 2;
            }
        }
    }

    //Entrega a aplicação e depois retorna os dados
	@Override
	public void dispose () {
	}
}
