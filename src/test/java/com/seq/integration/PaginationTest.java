package com.seq.integration;

import com.seq.TestUtils;
import com.seq.api.*;
import com.seq.http.Client;

import org.junit.Test;

import java.util.*;
import java.text.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PaginationTest {
  static Client client;
  static Key key;
  final static int PAGE_SIZE = 5;

  @Test
  public void run() throws Exception {
    testKeyPageCursor();
    testAccountPageCursor();
    testFlavorPageCursor();
    testTransactionPageCursor();
  }

  public void testKeyPageCursor() throws Exception {
    client = TestUtils.generateClient();

    ArrayList<String> ids = new ArrayList<String>();
    for (int i = 0; i < PAGE_SIZE + 1; i++) {
      String id = UUID.randomUUID().toString();
      ids.add(id);
      new Key.Builder().setId(id).create(client);
    }
    Key.Page page = new Key.QueryBuilder()
      .setIds(ids)
      .setPageSize(PAGE_SIZE)
      .getPage(client);
    assertEquals(5, page.items.size());
    assertEquals(false, page.lastPage);

    Key.Page page2 = new Key.QueryBuilder()
      .getPage(client, page.cursor);
    assertEquals(1, page2.items.size());
    assertEquals(true, page2.lastPage);
  }

  public void testAccountPageCursor() throws Exception {
    client = TestUtils.generateClient();
    key = new Key.Builder().create(client);

    String testFilter = UUID.randomUUID().toString();

    ArrayList<String> ids = new ArrayList<String>();
    for (int i = 0; i < PAGE_SIZE + 1; i++) {
      new Account.Builder()
        .setId(UUID.randomUUID().toString())
        .addKey(key)
        .addTag("filter", testFilter)
        .create(client);
    }
    Account.Page page = new Account.QueryBuilder()
      .setFilter("tags.filter = $1")
      .setFilterParameters(Arrays.asList(testFilter))
      .setPageSize(PAGE_SIZE)
      .getPage(client);
    assertEquals(5, page.items.size());
    assertEquals(false, page.lastPage);

    Account.Page page2 = new Account.QueryBuilder()
      .getPage(client, page.cursor);
    assertEquals(1, page2.items.size());
    assertEquals(true, page2.lastPage);
  }

  public void testFlavorPageCursor() throws Exception {
    client = TestUtils.generateClient();
    key = new Key.Builder().create(client);

    String testFilter = UUID.randomUUID().toString();

    ArrayList<String> ids = new ArrayList<String>();
    for (int i = 0; i < PAGE_SIZE + 1; i++) {
      new Flavor.Builder()
        .setId(UUID.randomUUID().toString())
        .addKey(key)
        .addTag("filter", testFilter)
        .create(client);
    }
    Flavor.Page page = new Flavor.QueryBuilder()
      .setFilter("tags.filter = $1")
      .setFilterParameters(Arrays.asList(testFilter))
      .setPageSize(PAGE_SIZE)
      .getPage(client);
    assertEquals(5, page.items.size());
    assertEquals(false, page.lastPage);

    Flavor.Page page2 = new Flavor.QueryBuilder()
      .getPage(client, page.cursor);
    assertEquals(1, page2.items.size());
    assertEquals(true, page2.lastPage);
  }

  public void testTransactionPageCursor() throws Exception {
    client = TestUtils.generateClient();
    key = new Key.Builder().create(client);

    Flavor flavor = new Flavor.Builder().addKey(key).create(client);
    Account account = new Account.Builder().addKey(key).create(client);

    String testFilter = UUID.randomUUID().toString();

    ArrayList<String> ids = new ArrayList<String>();
    for (int i = 0; i < PAGE_SIZE + 1; i++) {
      new Transaction.Builder()
          .addAction(
              new Transaction.Builder.Action.Issue()
                  .setFlavorId(flavor.id)
                  .setAmount(1)
                  .setDestinationAccountId(account.id))
          .addReferenceDataField("filter", testFilter)
          .transact(client);
    }
    Transaction.Page page = new Transaction.QueryBuilder()
      .setFilter("reference_data.filter = $1")
      .setFilterParameters(Arrays.asList(testFilter))
      .setPageSize(PAGE_SIZE)
      .getPage(client);
    assertEquals(5, page.items.size());
    assertEquals(false, page.lastPage);

    Transaction.Page page2 = new Transaction.QueryBuilder()
      .getPage(client, page.cursor);
    assertEquals(1, page2.items.size());
    assertEquals(true, page2.lastPage);
  }
}
