package fr.efrei.pokemon_tcg.models;

import fr.efrei.pokemon_tcg.constants.TypePokemon;
import jakarta.persistence.*;

@Entity
public class Pokemon {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String uuid;
	private String nom;
	private Integer niveau;

	@Enumerated(EnumType.STRING)
	private TypePokemon type;

	private int rarete;

	public Pokemon() {}

	public Pokemon(String nom, Integer niveau, TypePokemon type, int rarete) {
		this.nom = nom;
		this.niveau = niveau;
		this.type = type;
		this.rarete = rarete;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Integer getNiveau() {
		return niveau;
	}

	public void setNiveau(Integer niveau) {
		this.niveau = niveau;
	}

	public TypePokemon getType() {
		return type;
	}

	public void setType(TypePokemon type) {
		this.type = type;
	}

	public String getUuid() {
		return uuid;
	}

	public int getRarete() {
		return rarete;
	}

	public void setRarete(int rarete) {
		this.rarete = rarete;
	}
}