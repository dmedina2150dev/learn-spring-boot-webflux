package com.springboot.reactor.app.models;

public class UsuarioConComentario {

	private Usuario usuario;
	private Comentarios comentarios;
	
	public UsuarioConComentario(Usuario usuario, Comentarios comentarios) {
		this.usuario = usuario;
		this.comentarios = comentarios;
	}

	@Override
	public String toString() {
		return "UsuarioConComentario [usuario=" + usuario + ", comentarios=" + comentarios + "]";
	}
	
	
	
}
