import phylocompare.main.UpdateService;

module phylocompare {
	uses UpdateService;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires jloda_core;
	requires jloda_fx;
	requires jloda_phylogeny;
	requires splitstreesix;
	requires java.xml;
	requires java.sql;
	requires org.apache.commons.numbers.gamma;
	requires jdk.compiler;
	requires java.desktop;
	requires java.net.http;
	requires com.fasterxml.jackson.databind;

	exports phylocompare.main;
	exports phylocompare.window;
	exports phylocompare.view;
	exports phylocompare.model;

	opens phylocompare.main;
	opens phylocompare.window;
	opens phylocompare.algorithm;
	opens phylocompare.view;
	opens phylocompare.utils;
	opens phylocompare.trace;
	opens phylocompare.model;
}