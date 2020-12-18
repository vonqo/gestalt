using System.Collections;
using System.Collections.Generic;
using System.IO;
using UnityEngine;

/** 
* Map color to unity object and it's meterial property
* Author: vonqo
*/
public class Colorizer : MonoBehaviour
{
    private GameObject obj;
    private GameObject[] colorObjects = new GameObject[1000];
    private Color[] colors = new Color[1000];
    private string colorPath = "Assets/Resource/color2.txt";
    private string ScreenCapDirectory = "/Users/von/Desktop/mood_test/";

    // Start is called before the first frame update
    void Start()
    {
        StreamReader reader = new StreamReader(colorPath);
        int i = 0;
        while (!reader.EndOfStream && i < 1000)
        {
            colors[i] = parseColor(reader.ReadLine());
            i++;
        }
        Debug.Log("Total:" + i);
        reader.Close();

        obj = GameObject.Find("Sphere");

        float ii = 0;
        float row = 0;
        for (i = 0; i < 1000; i++, ii += 1.5f)
        {
            if (i % 32 == 0)
            {
                ii = 0;
                row += 1.5f;
            }
            colorObjects[i] = GameObject.Instantiate(obj);
            colorObjects[i].name = "Obj_" + i;
            colorObjects[i].transform.position = new Vector3(-10 + ii, 15, 20 - row);
            Renderer cubeRenderer = colorObjects[i].GetComponent<Renderer>();
            cubeRenderer.material.SetColor("_Color", colors[i]);
            cubeRenderer.material.SetColor("_EmissionColor", colors[i]);
        }
        Destroy(obj);
        Destroy(GameObject.Find("Sphere"));
    }

    // Update is called once per frame
    void Update()
    {
        if (Input.GetKeyDown(KeyCode.R)) {
            ScreenCapture.CaptureScreenshot(ScreenCapDirectory + "unity_test.png");
            Debug.Log("ScreenCapture Taken!");
            Debug.Log("ScreenCap Location: " + ScreenCapDirectory);
        }
    }

    Color parseColor(string line)
    {
        string[] strColors = line.Split(' ');
        int red = int.Parse(strColors[0]);
        int green = int.Parse(strColors[1]);
        int blue = int.Parse(strColors[2]);
        return new Color32((byte)red, (byte)green, (byte)blue, 255);
    }
}
