package com.bank.transaction.service.events;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection ="Usuario")
public class Usuario {
	
	private String id;
	private String dni;
	private String numeroCelular;	
}
