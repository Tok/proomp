# Proomp
A Clojure workspace for Stable Diffusion.

Runs on top of Python 3.9 using PyTorch with CUDA Toolkit 11.7.

## Reports
* [Unit Test Results](https://tok.github.io/proomp/test-report)
* [Cloverage Report](https://tok.github.io/proomp/cloverage)

## Installation
### Python
Install Python 3.9 with Pip https://www.python.org/downloads/.

### Python Dependencies
    pip install -r requirements.txt

### Download and prepare Stable Diffusion model
> &#x26a0;&#xfe0f; Requires an account on https://huggingface.co.

    cd models
    git lfs install
    git lfs clone https://huggingface.co/stabilityai/stable-diffusion-2-1

### Leiningen And Clojure
> &#x2139;  Use JDK 17 or similar, i.e. from https://adoptium.net/

Install Leiningen from https://leiningen.org/.

The Clojure version is set in Leiningens build file `project.clj`.

## Configuration
Set your `python-dir` and your `workspace-path` in `proomp.config`.

## Usage
* Check and adjust the parameters in `proomp.core`.
  * Set an [active-mode](https://github.com/Tok/proomp/blob/main/src/proomp/core.clj#L11).
      * `::images` Generates different images from a prompt. Can be used to find a good start seed.
      * `::animation` Generates frames for a prompt and a start seed.
      * `::video` Creates a video from the generated frames.
  * Set the prompt `text`
  * Optionally provide a `negative-text` prompt and change the additions.
  * Optionally change active [resolutions](https://github.com/Tok/proomp/blob/main/src/proomp/domain/image/resolution.clj#L46)

Run proomp.core in the context of Leiningen or with the VM arguments:
    --add-modules jdk.incubator.foreign,jdk.incubator.vector
    --enable-native-access=ALL-UNNAMED

## Trouble-Shooting
`proomp.core-test` contains tests to check if Cuda is available
and to check if PyTorch bindings are working.

In case of problems, consider the following:
- PyTorch needs to be installed with an active cuda toolkit.
- The console command `nvcc --version` should return `Cuda compilation tools, release 11.7`.

### Workaround for ValueError when instantiating the Stable Diffusion i2i pipeline
Provide feature_extractor and safety_checker from a v1-5 model:

    git lfs clone https://huggingface.co/runwayml/stable-diffusion-v1-5

Move `feature_extractor` and `safety_checker` to 2-1, then delete v1-5 again.
Set the missing features in stable-diffusion-2-1/model_index.json:

    "feature_extractor": ["transformers", "CLIPImageProcessor"],
    "safety_checker": ["stable_diffusion", "StableDiffusionSafetyChecker"],
