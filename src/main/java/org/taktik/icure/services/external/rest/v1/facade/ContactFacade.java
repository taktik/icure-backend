/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.facade;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.dto.filter.predicate.Predicate;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.entities.HealthElement;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.entities.embed.Service;
import org.taktik.icure.entities.embed.SubContact;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.ContactLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.logic.impl.filter.Filters;
import org.taktik.icure.services.external.rest.v1.dto.ContactDto;
import org.taktik.icure.services.external.rest.v1.dto.IcureDto;
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto;
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto;
import org.taktik.icure.services.external.rest.v1.dto.data.LabelledOccurenceDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain;
import org.taktik.icure.utils.FuzzyValues;
import org.taktik.icure.utils.ResponseUtils;

import javax.security.auth.login.LoginException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Path("/contact")
@Api(tags = { "contact" })
@Consumes({"application/json"})
@Produces({"application/json"})
public class ContactFacade implements OpenApiFacade {
    private static final Logger log = LoggerFactory.getLogger(ContactFacade.class);

    private MapperFacade mapper;
	private org.taktik.icure.logic.impl.filter.Filters filters;
    private ContactLogic contactLogic;
	private SessionLogic sessionLogic;

    @ApiOperation(
            value = "Create a contact with the current user",
            response = ContactDto.class,
            httpMethod = "POST",
            notes = "Returns an instance of created contact."
    )
    @POST
    public Response createContact(ContactDto c) {
        if (c == null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        Contact contact;
        try {
			// handling services' indexes
			handleServiceIndexes(c);

            contact = contactLogic.createContact(mapper.map(c, Contact.class));
        } catch (MissingRequirementsException e) {
            log.warn(e.getMessage(), e);
            return Response.status(400).type("text/plain").entity(e.getMessage()).build();
        }

        boolean succeed = (contact != null);
        if (succeed) {
            return Response.ok().entity(mapper.map(contact, ContactDto.class)).build();
        } else {
            return Response.status(500).type("text/plain").entity("Contact creation failed.").build();
        }
    }

	protected void handleServiceIndexes(ContactDto c) {
		Long max = 0l;
		Long baseIndex = 0l;
		boolean nullFound = false;
		if (c.getServices() != null) {
			for (ServiceDto service : c.getServices()) {
				Long index = service.getIndex();
				if (index == null) {
					nullFound = true;
					baseIndex = max + 1;
					max = 0l;
				}
				if (nullFound) {
					index = baseIndex + 1 + (index != null ? index : 0l);
					service.setIndex(index);
				}
				if (index != null && index > max) {
					max = index;
				}
			}
		} else {
			// no null pointer exception in Orika in case of no services
			c.setServices(Lists.newArrayList());
		}
	}

	@ApiOperation(
			value = "Get a contact",
			response = ContactDto.class,
			httpMethod = "GET",
			notes = ""
	)
	@GET
	@Path("/{contactId}")
	public Response getContact(@PathParam("contactId") String contactId) {
		if (contactId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Contact contact = contactLogic.getContact(contactId);

		boolean succeed = (contact != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(contact, ContactDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting Contact failed. Possible reasons: no such contact exists, or server error. Please try again or read the server log.").build();
		}
	}

	@ApiOperation(
			value = "Get contacts",
			response = ContactDto.class,
			responseContainer = "Array",
			httpMethod = "POST",
			notes = ""
	)
	@POST
	@Path("/byIds")
	public Response getContacts(ListOfIdsDto contactIds) {
		if (contactIds == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		List<Contact> contacts = contactLogic.getContacts(new HashSet<>(contactIds.getIds()));

		boolean succeed = (contacts != null);
		if (succeed) {
			return Response.ok().entity(contacts.stream().map(c->mapper.map(c, ContactDto.class)).collect(Collectors.toList())).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting Contact failed. Possible reasons: no such contact exists, or server error. Please try again or read the server log.").build();
		}
	}

	@ApiOperation(
            value = "Get an empty content",
            response = ContentDto.class,
            httpMethod = "GET",
            notes = ""
    )
    @GET
    @Path("/service/content/empty")
    public ContentDto getEmptyContent() {
        return new ContentDto();
    }

    @ApiOperation(
			value = "Get the list of all used codes frequencies in services",
			response = LabelledOccurenceDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = ""
	)
	@GET
	@Path("/service/codes/{codeType}/{minOccurences}")
	public Response getServiceCodesOccurences(@PathParam("codeType") String codeType, @PathParam("minOccurences") Long minOccurences) {
		return Response.ok().entity(contactLogic.getServiceCodesOccurences(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId(),codeType,minOccurences)).build();
	}

	@ApiOperation(
            value = "List contacts found By Healthcare Party and form Id.",
            response = ContactDto.class,
            responseContainer = "Array",
            httpMethod = "GET"
    )
    @GET
    @Path("/byHcPartyFormId")
    public Response findByHCPartyFormId(@QueryParam("hcPartyId") String hcPartyId, @QueryParam("formId") String formId) {
        if (hcPartyId == null || formId == null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        List<Contact> contactList = contactLogic.findContactsByHCPartyFormId(hcPartyId, formId);

        boolean succeed = (contactList != null);
        if (succeed) {

            // mapping to Dto
            List<ContactDto> contactDtoList = contactList.stream().map(contact -> mapper.map(contact, ContactDto.class)).collect(Collectors.toList());
            return Response.ok().entity(contactDtoList).build();
        } else {
            return Response.status(500).type("text/plain").entity("Getting Contacts failed. Please try again or read the server log.").build();
        }
    }

	@ApiOperation(
			value = "List contacts found By Healthcare Party and form Id.",
			response = ContactDto.class,
			responseContainer = "Array",
			httpMethod = "POST"
	)
	@POST
	@Path("/byHcPartyFormIds")
	public Response findByHCPartyFormIds(@QueryParam("hcPartyId") String hcPartyId, ListOfIdsDto formIds) {
		if (hcPartyId == null || formIds == null || formIds.getIds().size()==0) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		List<Contact> contactList = contactLogic.findContactsByHCPartyFormIds(hcPartyId, formIds.getIds());

		boolean succeed = (contactList != null);
		if (succeed) {

			// mapping to Dto
			List<ContactDto> contactDtoList = contactList.stream().map(contact -> mapper.map(contact, ContactDto.class)).collect(Collectors.toList());
			return Response.ok().entity(contactDtoList).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting Contacts failed. Please try again or read the server log.").build();
		}
	}

	@ApiOperation(
            value = "List contacts found By Healthcare Party and secret foreign keys.",
            response = ContactDto.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = "Keys must be delimited by coma"
    )
    @GET
    @Path("/byHcPartySecretForeignKeys")
    public Response findByHCPartyPatientSecretFKeys(@QueryParam("hcPartyId") String hcPartyId,
                                                    @QueryParam("secretFKeys") String secretFKeys,
                                                    @QueryParam("planOfActionIds") String planOfActionsIds, @QueryParam("skipClosedContacts") Boolean skipClosedContacts) {
        if (hcPartyId == null || secretFKeys == null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        Set<String> secretPatientKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toSet());
        List<Contact> contactList = contactLogic.findByHCPartyPatient(hcPartyId, new ArrayList<>(secretPatientKeys));

        boolean succeed = (contactList != null);
        if (succeed) {
			List<ContactDto> contactDtoList;
            if(planOfActionsIds != null) {
                List<String> poaids = Arrays.asList(planOfActionsIds.split(","));
				contactDtoList = contactList.stream().filter(c->(skipClosedContacts==null||!skipClosedContacts||c.getClosingDate()==null)&&!Collections.disjoint(c.getSubContacts().stream().map(SubContact::getPlanOfActionId).collect(Collectors.toSet()),poaids)).map(contact -> mapper.map(contact, ContactDto.class)).collect(Collectors.toList());
            } else {
				contactDtoList = contactList.stream().filter(c->skipClosedContacts==null||!skipClosedContacts||c.getClosingDate()==null).map(contact -> mapper.map(contact, ContactDto.class)).collect(Collectors.toList());
			}

            return Response.ok().entity(contactDtoList).build();
        } else {
            return Response.status(500).type("text/plain").entity("Getting Contacts failed. Please try again or read the server log.").build();
        }
    }

	@ApiOperation(
			value = "List contacts found By Healthcare Party and secret foreign keys.",
			response = ContactDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Keys must be delimited by coma"
	)
	@GET
	@Path("/byHcPartySecretForeignKeys/delegations")
	public Response findDelegationsStubsByHCPartyPatientSecretFKeys(@QueryParam("hcPartyId") String hcPartyId,
	                                                @QueryParam("secretFKeys") String secretFKeys) {
		if (hcPartyId == null || secretFKeys == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> secretPatientKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toSet());
		return Response.ok().entity(contactLogic.findByHCPartyPatient(hcPartyId, new ArrayList<>(secretPatientKeys)).stream().map(contact -> mapper.map(contact, IcureStubDto.class)).collect(Collectors.toList())).build();
	}

	@ApiOperation(
			value = "Update delegations in healthElements.",
			httpMethod = "POST",
			notes = "Keys must be delimited by coma"
	)
	@POST
	@Path("/delegations")
	public Response setContactsDelegations(List<IcureStubDto> stubs) throws Exception {
		List<Contact> contacts = contactLogic.getContacts(stubs.stream().map(IcureDto::getId).collect(Collectors.toList()));
		contacts.forEach(contact -> stubs.stream().filter(s -> s.getId().equals(contact.getId())).findFirst().ifPresent(stub -> {
			stub.getDelegations().forEach((s, delegationDtos) -> contact.getDelegations().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
			stub.getEncryptionKeys().forEach((s, delegationDtos) -> contact.getEncryptionKeys().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
			stub.getCryptedForeignKeys().forEach((s, delegationDtos) -> contact.getCryptedForeignKeys().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
		}));
		contactLogic.updateEntities(contacts);
		return Response.ok().build();
	}


	@ApiOperation(
            value = "Close contacts for Healthcare Party and secret foreign keys.",
            response = ContactDto.class,
            responseContainer = "Array",
            httpMethod = "PUT",
            notes = "Keys must be delimited by coma"
    )
    @PUT
    @Path("/byHcPartySecretForeignKeys/close")
    public Response closeForHCPartyPatientSecretFKeys(@QueryParam("hcPartyId") String hcPartyId, @QueryParam("secretFKeys") String secretFKeys) {
        if (hcPartyId == null || secretFKeys == null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        List<String> secretPatientKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toList());
        List<Contact> contactList = contactLogic.findByHCPartyPatient(hcPartyId, secretPatientKeys);

        List<Contact> result = new ArrayList<>();

        boolean succeed = (contactList != null);
        if (succeed) {

            for (Contact c : contactList) {
                if (c.getClosingDate()==null) {
                    result.add(c);
                    c.setClosingDate(FuzzyValues.getFuzzyDateTime(LocalDateTime.now(), ChronoUnit.SECONDS));
                    try {
                        contactLogic.modifyContact(c);
                    } catch (MissingRequirementsException e) {
                        log.warn(e.getMessage(), e);
                        return Response.status(400).type("text/plain").entity(e.getMessage()).build();
                    }
                }
            }

            // mapping to Dto
            List<ContactDto> contactDtoList = result.stream().map(contact -> mapper.map(contact, ContactDto.class)).collect(Collectors.toList());
            return Response.ok().entity(contactDtoList).build();
        } else {
            return Response.status(500).type("text/plain").entity("Getting Contacts failed. Please try again or read the server log.").build();
        }
    }

    @ApiOperation(
            value = "Delete contacts.",
            response = String.class,
            responseContainer = "Array",
            httpMethod = "DELETE",
            notes = "Response is a set containing the ID's of deleted contacts."
    )
    @DELETE
    @Path("/{contactIds}")
    public Response deleteContacts(@PathParam("contactIds") String ids) {
        if (ids == null || ids.length() == 0) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        // TODO versioning?

        Set<String> deletedIds = contactLogic.deleteContacts(new HashSet<>(Arrays.asList(ids.split(","))));

        boolean succeed = (deletedIds != null);
        if (succeed) {
            return Response.ok().entity(deletedIds).build();
        } else {
            return Response.status(500).type("text/plain").entity("Contacts deletion failed.").build();
        }
    }

    @ApiOperation(
            value = "Modify a contact",
            response = ContactDto.class,
            httpMethod = "PUT",
            notes = "Returns the modified contact."
    )
    @PUT
    public Response modifyContact(ContactDto contactDto) {
        if (contactDto == null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        try {
			handleServiceIndexes(contactDto);

            contactLogic.modifyContact(mapper.map(contactDto, Contact.class));
            Contact modifiedContact = contactLogic.getContact(contactDto.getId());

            boolean succeed = (modifiedContact != null);
            if (succeed) {
                return Response.ok().entity(mapper.map(modifiedContact, ContactDto.class)).build();
            } else {
                return Response.status(500).type("text/plain").entity("Contact modification failed.").build();
            }
        } catch (MissingRequirementsException e) {
            log.warn(e.getMessage(), e);
            return Response.status(400).type("text/plain").entity(e.getMessage()).build();
        }
    }

    @ApiOperation(
            value = "Modify a batch of contacts",
            response = ContactDto.class,
            responseContainer = "Array",
            httpMethod = "PUT",
            notes = "Returns the modified contacts."
    )
    @PUT
    @Path("/batch")
    public Response modifyContacts(List<ContactDto> contactDtos) {
        if (contactDtos == null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        try {
            contactDtos.forEach ( c -> handleServiceIndexes(c) );

            List<Contact> contacts = contactLogic.updateEntities(contactDtos.stream().map(f -> mapper.map(f, Contact.class)).collect(Collectors.toList()));
            return Response.ok().entity(contacts.stream().map(f -> mapper.map(f, ContactDto.class)).collect(Collectors.toList())).build();

        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return Response.status(400).type("text/plain").entity(e.getMessage()).build();
        }
    }

    @ApiOperation(
            value = "Delegates a contact to a healthcare party",
            response = ContactDto.class,
            httpMethod = "POST",
            notes = "It delegates a contact to a healthcare party (By current healthcare party). Returns the contact with new delegations."
    )
    @POST
    @Path("/{contactId}/delegate")
    public Response newDelegations(@PathParam("contactId") String contactId, DelegationDto d) {
        if (contactId == null || d == null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

	    contactLogic.addDelegation(contactId, mapper.map(d, Delegation.class));
        Contact contactWithDelegation = contactLogic.getContact(contactId);

        // TODO: write more function to add complete access to all contacts of a patient to a HcParty, or a contact or a subset of contacts
        // TODO: kind of adding new secretForeignKeys and cryptedForeignKeys

        boolean succeed = (contactWithDelegation != null && contactWithDelegation.getDelegations() != null && contactWithDelegation.getDelegations().size() > 0);
        if (succeed) {
            return Response.ok().entity(mapper.map(contactWithDelegation, ContactDto.class)).build();
        } else {
            return Response.status(500).type("text/plain").entity("Delegation creation for Contact failed.").build();
        }
    }

    @ApiOperation(
            value = "List contacts for the current user (HcParty) or the given hcparty in the filter ",
            response = org.taktik.icure.services.external.rest.v1.dto.ContactPaginatedList.class,
            httpMethod = "POST",
            notes = "Returns a list of contacts along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page."
    )
    @POST
    @Path("/filter")
    public Response filterBy(
            @ApiParam(value = "The start key for pagination, depends on the filters used. If multiple keys are used, the keys are delimited by coma", required = false) @QueryParam("startKey") String startKey,
            @ApiParam(value = "A Contact document ID", required = false) @QueryParam("startDocumentId") String startDocumentId,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") Integer limit,
            FilterChain filterChain) {

        Response response;

        ArrayList startKeyList = null;
        if (startKey != null && startKey.length() > 0) {
            startKeyList = new ArrayList<>(Splitter.on(",").omitEmptyStrings().trimResults().splitToList(startKey));
        }

        PaginationOffset paginationOffset = new PaginationOffset(startKeyList, startDocumentId, null, limit);

        PaginatedList<Contact> contacts;
        if (filterChain != null) {
            contacts = contactLogic.filterContacts(paginationOffset, new org.taktik.icure.dto.filter.chain.FilterChain(filterChain.getFilter(), mapper.map(filterChain.getPredicate(), Predicate.class)));
        } else {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        if (contacts != null) {
            if (contacts.getRows() == null) {
                contacts.setRows(new ArrayList<>());
            }

            org.taktik.icure.services.external.rest.v1.dto.PaginatedList<ContactDto> paginatedContactDtoList =
                    new org.taktik.icure.services.external.rest.v1.dto.PaginatedList<>();
            mapper.map(
                    contacts,
                    paginatedContactDtoList,
                    new TypeBuilder<PaginatedList<Contact>>() {
                    }.build(),
                    new TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<ContactDto>>() {
                    }.build()
            );
            response = ResponseUtils.ok(paginatedContactDtoList);
        } else {
            response = ResponseUtils.internalServerError("Listing and Filtering contacts failed.");
        }

        return response;
    }

	@ApiOperation(
			value = "Get ids of contacts matching the provided filter for the current user (HcParty) ",
			response = String.class,
			responseContainer = "Array",
			httpMethod = "POST"
	)
	@POST
	@Path("/match")
	public List<String> matchBy(Filter filter) throws LoginException {
		return new ArrayList<>(filters.resolve(filter));
	}

	@ApiOperation(
            value = "List services for the current user (HcParty) or the given hcparty in the filter ",
            response = org.taktik.icure.services.external.rest.v1.dto.ServicePaginatedList.class,
            httpMethod = "POST",
            notes = "Returns a list of contacts along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page."
    )
    @POST
    @Path("/service/filter")
    public Response filterServicesBy(
            @ApiParam(value = "The start key for pagination, depends on the filters used. If multiple keys are used, the keys are delimited by coma", required = false) @QueryParam("startKey") String startKey,
            @ApiParam(value = "A Contact document ID", required = false) @QueryParam("startDocumentId") String startDocumentId,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") Integer limit,
            FilterChain filterChain) {

        Response response;

        ArrayList startKeyList = null;
        if (startKey != null && startKey.length() > 0) {
            startKeyList = new ArrayList<>(Splitter.on(",").omitEmptyStrings().trimResults().splitToList(startKey));
        }

        PaginationOffset paginationOffset = new PaginationOffset(startKeyList, startDocumentId, null, limit);

        PaginatedList<Service> services;
        if (filterChain != null) {
            services = contactLogic.filterServices(paginationOffset, new org.taktik.icure.dto.filter.chain.FilterChain(filterChain.getFilter(), mapper.map(filterChain.getPredicate(), Predicate.class)));
        } else {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        if (services != null) {
            if (services.getRows() == null) {
                services.setRows(new ArrayList<>());
            }

            org.taktik.icure.services.external.rest.v1.dto.PaginatedList<ServiceDto> paginatedServiceDtoList =
                    new org.taktik.icure.services.external.rest.v1.dto.PaginatedList<>();
            mapper.map(
                    services,
                    paginatedServiceDtoList,
                    new TypeBuilder<PaginatedList<Service>>() {
                    }.build(),
                    new TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<ServiceDto>>() {
                    }.build()
            );
            response = ResponseUtils.ok(paginatedServiceDtoList);
        } else {
            response = ResponseUtils.internalServerError("Listing and Filtering services failed.");
        }

        return response;
    }

    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

	@Context
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Context
	public void setContactLogic(ContactLogic contactLogic) {
		this.contactLogic = contactLogic;
	}

	@Context
	public void setFilters(Filters filters) {
		this.filters = filters;
	}

	@ExceptionHandler(Exception.class)
    Response exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseUtils.internalServerError(e.getMessage());
    }

}
