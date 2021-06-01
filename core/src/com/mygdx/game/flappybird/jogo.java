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

    //Imagens essenciais de tela, cano e fundo
    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoSuperior;
    private Texture canoInferior;
    private Texture gameOver;

    //Variaveis do Jogo(Pontuação maior, atual e estado do jogo)
    private int pontuacaoMaxima = 0;
    private int pontos = 0;
    private int estadoJogo = 0;

    //Tela do dispositivo
    private float larguraDispositivo;
    private float alturaDispositivo;

    //Posição dos canos e seu espaço
    private float posicaoCanoHorizontal = 0;
    private float posicaoCanoVertical;
    private float espaçoEntreCanos;

    //Variaveis de minimo e máximo pro randomizador
    private int minimo = 1100;
    private int maximo = 1300;

    //Randomizador dos canos
    private Random random;

    //Variação da animação do Flappy
    private float variacao = 0;
    //Gravidade
    private float gravidade = 0;

    //Posicionamento do Flappy
    private float posicaoInicialVerticalPassaro = 0;
    private float posicaoHorizontalPassaro = 0;

    //Variaveis de texto
    BitmapFont textoPontuacao;
    BitmapFont textoReiniciar;
    BitmapFont textoMelhorPontuacao;

    //Arquivos de som
    Sound somColisao;
    Sound somVoar;
    Sound somPontos;

    //Variavel que vai servir pra pontuação se passar
    private boolean passouCano = false;

    //Variaveis de colisão
    private ShapeRenderer shapeRenderer;
    private Circle circulopassaro;
    private Rectangle retanguloCanoSuperior;
    private Rectangle retanguloCanoInferior;

    Preferences preferencias;

    //Instancia das imagens
    @Override
    public void create () {
        inicializaImagens();
        inicializaTela();
    }
    //Renderização da interface do jogo
    @Override
    public void render () {
        verificarEstadoJogo();
        validarPontos();
        desenhaImagens();
        detectarColisao();
    }


    private void inicializaTela() {
        batch = new SpriteBatch();

        random = new Random();
        //Tamanho do dispositivo
        larguraDispositivo = Gdx.graphics.getWidth();
        alturaDispositivo = Gdx.graphics.getHeight();
        //Posicição inicial do Flappy
        posicaoInicialVerticalPassaro = alturaDispositivo/2;
        //Posição do cano Horizontal inicial
        posicaoCanoHorizontal = larguraDispositivo;
        //Espaço entre os canos
        espaçoEntreCanos = 350;


        //Inicialização dos textos
        textoPontuacao = new BitmapFont();
        textoPontuacao.setColor( Color.WHITE);
        textoPontuacao.getData().setScale(10);

        textoMelhorPontuacao = new BitmapFont();
        textoMelhorPontuacao.setColor(Color.GREEN);
        textoMelhorPontuacao.getData().setScale(3);

        textoReiniciar = new BitmapFont();
        textoReiniciar.setColor(Color.PINK);
        textoReiniciar.getData().setScale(3);

        //Inicialização dos colisores
        shapeRenderer = new ShapeRenderer();
        circulopassaro = new Circle();
        retanguloCanoSuperior = new Rectangle();
        retanguloCanoInferior = new Rectangle();

        //Arquivos de som
        somVoar = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
        somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
        somPontos = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

        //Preferências do jogo e sua pontuação máxima
        preferencias = Gdx.app.getPreferences("flappyBird");
        pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);
    }

    private void inicializaImagens() {
        //Imagens para animação do pássaro
        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");
        //Imagem de fundo e dos canos e de gameover
        fundo = new Texture("fundo.png" );
        canoSuperior = new Texture("cano_topo_maior.png");
        canoInferior = new Texture("cano_baixo_maior.png");
        gameOver = new Texture("game_over.png");
    }

    private void detectarColisao(){

        //Criação dos colisores dos pássaros e dos canos
        circulopassaro.set(50 + passaros[0].getWidth() / 2, posicaoInicialVerticalPassaro + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);
        retanguloCanoSuperior.set(posicaoCanoHorizontal, alturaDispositivo / 2 + espaçoEntreCanos/ 2 + posicaoCanoVertical, canoSuperior.getWidth(), canoSuperior.getHeight() );
        retanguloCanoInferior.set(posicaoCanoHorizontal, alturaDispositivo / 2 - canoInferior.getHeight() - espaçoEntreCanos / 2 + posicaoCanoVertical, canoInferior.getWidth(), canoInferior.getHeight());

        //Variaveis de detecção das colisões
        boolean colisaoCanoSuperior = Intersector.overlaps(circulopassaro, retanguloCanoSuperior);
        boolean colisaoCanoInferior = Intersector.overlaps(circulopassaro, retanguloCanoInferior);

        // Se houve colisão o jogo acaba
        if (colisaoCanoInferior || colisaoCanoSuperior)
        {
            if(estadoJogo ==1){
                somColisao.play();
                estadoJogo = 2;
            }
        }

    }
    private void verificarEstadoJogo() {
        //Verifica o toque na tela
        boolean toqueTela = Gdx.input.justTouched();
        //Se o estado for 0, tela de inicio
        if (estadoJogo == 0) {
            //Se tocar inicia o jogo
            if (toqueTela)
            {
                gravidade = -20;
                estadoJogo = 1;
                somVoar.play();
            }
            //Se o estado do jogo for 1, o jogo está rodando
        } else if (estadoJogo == 1) {
            //Pega o touch e faz o bicho pular e tocar o som
            if (toqueTela) {
                gravidade = -20;
                somVoar.play();
            }
            //Faz os canos andarem
            posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 300;
            //Se a posição do cano estiver na frente de onde ele está, ou seja, não passou ele vai spawnar aleatoriamente numa posição respeitando as variáveis de minimo e máximo
            if (posicaoCanoHorizontal < -canoInferior.getWidth()) {
                posicaoCanoHorizontal = larguraDispositivo;
                posicaoCanoHorizontal = random.nextInt( (maximo - minimo) + 1 ) + minimo;
                passouCano = false;
            }

            //Diminuição da gravidade quando toca na tela
            if (posicaoInicialVerticalPassaro > 0 || toqueTela)
                posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
            gravidade++;
            //Se o estado for fim de jogo
        } else if (estadoJogo == 2) {
            //Para mudar a pontuação máxima pela nova máxima
            if (pontos > pontuacaoMaxima) {
                pontuacaoMaxima = pontos;
                //Salvando a pontuação nas preferencias
                preferencias.putInteger( "pontuacaoMaxima", pontuacaoMaxima );
            }
            //É o que faz o passarinho "andar" só que aqui ele parou depois de bater
            posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500;

            //Resetando os valores para o inicial
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
    private void desenhaImagens() {
        //Executar
        batch.begin();
        //Desenha o fundo de acordo com a largura e altura do dispositivo
        batch.draw(fundo,0,0,larguraDispositivo,alturaDispositivo);
        //Desenha o passarinho vuano
        batch.draw(passaros[(int) variacao],50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro);
        //Desenha os canos de cima e de baixo
        batch.draw( canoInferior, posicaoCanoHorizontal , alturaDispositivo/2 - canoInferior.getHeight() - espaçoEntreCanos/2 + posicaoCanoVertical);
        batch.draw( canoSuperior, posicaoCanoHorizontal ,alturaDispositivo/2 + espaçoEntreCanos/2 + posicaoCanoVertical);
        //Desenha o texto de pontuação de acordo com a pontuação
        textoPontuacao.draw( batch, String.valueOf( pontos ),larguraDispositivo /2, alturaDispositivo - 100 );//desenha a pontuação no topo da tela
        //Desenha a tela de game over com seus parâmetros de tocar a tela e a pontuação
        if(estadoJogo == 2)//se o estado for 2, é game over, onde se instancia as frases de game over, reinicio e melhor pontuação
        {
            batch.draw(gameOver, larguraDispositivo / 2 +200 - gameOver.getWidth(), alturaDispositivo / 2);
            textoReiniciar.draw(batch, "TOQUE PARA REINICIAR", larguraDispositivo / 2 - 250, alturaDispositivo /2 - gameOver.getHeight() / 2);
            textoMelhorPontuacao.draw(batch, "MELHOR PONTUAÇÃO: \n" + pontuacaoMaxima +" Pontos", larguraDispositivo /2 - 250, alturaDispositivo /2 - gameOver.getHeight() * 2);
        }
        //Termina execução
        batch.end();
    }

    private void validarPontos() {
        //Se a posição do cano for menor que a posição do passarinho
        if (posicaoCanoHorizontal < 50 - passaros[0].getWidth())
        {
            //E o passou cano for false ele vai adicionar o ponto
            if (!passouCano){
                pontos++;
                passouCano = true;
                somPontos.play();
            }
        }
        //É o que que tá fazendo a alteração das animações pelo tempo
        variacao += Gdx.graphics.getDeltaTime() * 10;
        if(variacao > 3)
            variacao = 0;
    }

    @Override
    public void dispose () {

    }
}
